package org.example.ftblr.Services.impl;

import org.example.ftblr.Entity.*;
import org.example.ftblr.Repository.*;
import org.example.ftblr.Services.MatchService;
import org.example.ftblr.Services.NotificationService;
import org.example.ftblr.dtos.*;
import org.example.ftblr.exception.BusinessException;
import org.example.ftblr.exception.ResourceNotFoundException;
import org.example.ftblr.mapper.MatchMapper;
import org.example.ftblr.mapper.TeamMapper;
import org.example.ftblr.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - MatchServiceImpl")
class MatchServiceImplTest {

    @Mock private MatchRepository matchRepository;
    @Mock private TerrainRepository terrainRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private MatchMapper matchMapper;
    @Mock private TeamMapper teamMapper;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;

    @InjectMocks
    private MatchServiceImpl matchService;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;
    @Mock private UserDetailsImpl userDetails;


    private UUID matchId;
    private UUID team1Id;
    private UUID team2Id;
    private UUID terrainId;
    private UUID userId;

    private Match match;
    private MatchDTO matchDTO;
    private Team team1;
    private Team team2;
    private Terrain terrain;
    private User adminUser;
    private User organizateurUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        matchId   = UUID.randomUUID();
        team1Id   = UUID.randomUUID();
        team2Id   = UUID.randomUUID();
        terrainId = UUID.randomUUID();
        userId    = UUID.randomUUID();


        terrain = new Terrain();
        terrain.setId(terrainId);
        terrain.setName("Terrain Alpha");
        terrain.setIsActive(true);


        team1 = new Team();
        team1.setId(team1Id);
        team1.setName("Équipe A");
        team1.setIsActive(true);

        team2 = new Team();
        team2.setId(team2Id);
        team2.setName("Équipe B");
        team2.setIsActive(true);


        adminUser = new User();
        adminUser.setId(userId);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(Role.ADMIN);

        organizateurUser = new User();
        organizateurUser.setId(UUID.randomUUID());
        organizateurUser.setEmail("orga@test.com");
        organizateurUser.setRole(Role.ORGANIZATEUR);

        regularUser = new User();
        regularUser.setId(UUID.randomUUID());
        regularUser.setEmail("player@test.com");
        regularUser.setRole(Role.JOUEUR);

        match = Match.builder()
                .id(matchId)
                .titre("Match Test")
                .time(LocalDateTime.now().plusDays(2))
                .matchType(MatchType.FRIENDLY)
                .playersNeeded(10)
                .currentPlayers(5)
                .requiredLevel(RequiredLevel.INTERMEDIATE)
                .cout(new BigDecimal("50.00"))
                .visibility(Visibility.PUBLIC)
                .status(StatusMatch.SCHEDULED)
                .terrain(terrain)
                .team1(team1)
                .team2(team2)
                .createdBy(adminUser)
                .participations(new ArrayList<>())
                .build();

        matchDTO = MatchDTO.builder()
                .titre("Match Test")
                .time(LocalDateTime.now().plusDays(2))
                .matchType(MatchType.FRIENDLY)
                .playersNeeded(10)
                .currentPlayers(5)
                .requiredLevel(RequiredLevel.INTERMEDIATE)
                .cout(new BigDecimal("50.00"))
                .visibility(Visibility.PUBLIC)
                .status(StatusMatch.SCHEDULED)
                .terrainId(terrainId)
                .team1Id(team1Id)
                .team2Id(team2Id)
                .build();

        setupSecurityContext(adminUser);
    }

    private void setupSecurityContext(User user) {
        lenient().when(userDetails.getId()).thenReturn(user.getId());
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    private void stubValidTerrain() {
        when(terrainRepository.findById(terrainId)).thenReturn(Optional.of(terrain));
    }

    private void stubValidTeams() {
        when(teamRepository.findById(team1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(team2Id)).thenReturn(Optional.of(team2));
    }

    private void stubNoTeamConflict() {
        when(teamRepository.isTeamBusy(eq(team1Id), any())).thenReturn(false);
        when(teamRepository.isTeamBusy(eq(team2Id), any())).thenReturn(false);
    }

    private void stubNoTerrainConflict() {
        when(matchRepository.isTerrainBusy(eq(terrainId), any())).thenReturn(false);
    }

    private void stubNoDuplicateMatch() {
        when(matchRepository.existsByTeam1AndTeam2AndTime(any(), any(), any())).thenReturn(false);
    }


    @Nested
    @DisplayName("createMatch()")
    class CreateMatchTests {

        @Test
        @DisplayName("Admin peut créer un match avec des données valides")
        void givenAdminUser_whenCreateMatch_thenSuccess() {
            stubValidTerrain();
            stubValidTeams();
            stubNoTeamConflict();
            stubNoTerrainConflict();
            stubNoDuplicateMatch();

            when(matchMapper.toEntity(matchDTO)).thenReturn(match);
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            MatchDTO result = matchService.createMatch(matchDTO);

            assertThat(result).isNotNull();
            verify(matchRepository).save(match);
            verify(matchMapper).toDTO(match);
        }

        @Test
        @DisplayName("Organisateur peut créer un match")
        void givenOrganisateurUser_whenCreateMatch_thenSuccess() {
            setupSecurityContext(organizateurUser);
            stubValidTerrain();
            stubValidTeams();
            stubNoTeamConflict();
            stubNoTerrainConflict();
            stubNoDuplicateMatch();

            when(matchMapper.toEntity(matchDTO)).thenReturn(match);
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            MatchDTO result = matchService.createMatch(matchDTO);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Un joueur ne peut pas créer un match")
        void givenRegularUser_whenCreateMatch_thenThrowsBusinessException() {
            setupSecurityContext(regularUser);

            assertThatThrownBy(() -> matchService.createMatch(matchDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("organisateurs");
        }

        @Test
        @DisplayName("Terrain inexistant → ResourceNotFoundException")
        void givenInvalidTerrain_whenCreateMatch_thenThrows() {
            when(terrainRepository.findById(terrainId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> matchService.createMatch(matchDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Terrain not found");
        }

        @Test
        @DisplayName("Terrain inactif → BusinessException")
        void givenInactiveTerrain_whenCreateMatch_thenThrows() {
            terrain.setIsActive(false);
            when(terrainRepository.findById(terrainId)).thenReturn(Optional.of(terrain));

            assertThatThrownBy(() -> matchService.createMatch(matchDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Terrain is not active");
        }

        @Test
        @DisplayName("Même équipe pour team1 et team2 → BusinessException")
        void givenSameTeams_whenCreateMatch_thenThrows() {
            matchDTO.setTeam2Id(team1Id);
            when(terrainRepository.findById(terrainId)).thenReturn(Optional.of(terrain));
            when(teamRepository.findById(team1Id)).thenReturn(Optional.of(team1));

            assertThatThrownBy(() -> matchService.createMatch(matchDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("same team");
        }

        @Test
        @DisplayName("Team1 déjà occupée → BusinessException")
        void givenBusyTeam1_whenCreateMatch_thenThrows() {
            stubValidTerrain();
            stubValidTeams();
            when(teamRepository.isTeamBusy(eq(team1Id), any())).thenReturn(true);

            assertThatThrownBy(() -> matchService.createMatch(matchDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already scheduled");
        }

        @Test
        @DisplayName("Terrain déjà réservé → BusinessException")
        void givenBusyTerrain_whenCreateMatch_thenThrows() {
            stubValidTerrain();
            stubValidTeams();
            stubNoTeamConflict();
            when(matchRepository.isTerrainBusy(eq(terrainId), any())).thenReturn(true);

            assertThatThrownBy(() -> matchService.createMatch(matchDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Terrain is already booked");
        }

        @Test
        @DisplayName("Match en double (mêmes équipes, même heure) → BusinessException")
        void givenDuplicateMatch_whenCreateMatch_thenThrows() {
            stubValidTerrain();
            stubValidTeams();
            stubNoTeamConflict();
            stubNoTerrainConflict();
            when(matchRepository.existsByTeam1AndTeam2AndTime(any(), any(), any())).thenReturn(true);

            assertThatThrownBy(() -> matchService.createMatch(matchDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("getMatchById()")
    class GetMatchByIdTests {

        @Test
        @DisplayName("Retourne le MatchDTO quand le match existe")
        void givenExistingMatch_whenGetById_thenReturnDTO() {
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            MatchDTO result = matchService.getMatchById(matchId);

            assertThat(result).isNotNull();
            assertThat(result.getTitre()).isEqualTo("Match Test");
        }

        @Test
        @DisplayName("Match inexistant → ResourceNotFoundException")
        void givenNonExistingMatch_whenGetById_thenThrows() {
            when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> matchService.getMatchById(matchId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Match not found");
        }

        @Test
        @DisplayName("Les participations sont bien mappées")
        void givenMatchWithParticipations_whenGetById_thenParticipationsMapped() {
            User participant = new User();
            participant.setId(UUID.randomUUID());
            participant.setFirstName("Ali");
            participant.setLastName("Hassan");
            participant.setPosition(PositionStatus.MILIEU);
            participant.setSkillLevel(SkillLevel.INTERMEDIATE);

            MatchParticipation participation = new MatchParticipation();
            participation.setId(UUID.randomUUID());
            participation.setUser(participant);
            participation.setTeam(team1);
            participation.setStatus(ParticipationStatus.CONFIRMED);
            participation.setIsPaid(true);

            match.getParticipations().add(participation);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);
            when(teamMapper.toDTO(team1)).thenReturn(new TeamDTO());

            MatchDTO result = matchService.getMatchById(matchId);

            assertThat(result).isNotNull();
        }
    }


    @Nested
    @DisplayName("getAllMatches()")
    class GetAllMatchesTests {

        @Test
        @DisplayName("Retourne la liste complète des matchs")
        void whenGetAllMatches_thenReturnList() {
            List<Match> matches = List.of(match);
            List<MatchDTO> dtos = List.of(matchDTO);

            when(matchRepository.findAll()).thenReturn(matches);
            when(matchMapper.toDTOList(matches)).thenReturn(dtos);

            List<MatchDTO> result = matchService.getAllMatches();

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Liste vide → retourne liste vide")
        void whenNoMatches_thenReturnEmptyList() {
            when(matchRepository.findAll()).thenReturn(Collections.emptyList());
            when(matchMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<MatchDTO> result = matchService.getAllMatches();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateMatch()")
    class UpdateMatchTests {

        @Test
        @DisplayName("Met à jour un match sans changement de terrain/équipe/heure")
        void givenNoFieldChange_whenUpdateMatch_thenSuccess() {
            matchDTO.setTerrainId(terrainId);
            matchDTO.setTeam1Id(team1Id);
            matchDTO.setTeam2Id(team2Id);
            matchDTO.setTime(match.getTime());

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            doNothing().when(matchMapper).updateEntityFromDTO(matchDTO, match);
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            MatchDTO result = matchService.updateMatch(matchId, matchDTO);

            assertThat(result).isNotNull();
            verify(matchRepository).save(match);
        }

        @Test
        @DisplayName("Match complété → impossible de modifier")
        void givenCompletedMatch_whenUpdateMatch_thenThrows() {
            match.setStatus(StatusMatch.COMPLETED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.updateMatch(matchId, matchDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Cannot update a completed match");
        }

        @Test
        @DisplayName("Match inexistant → ResourceNotFoundException")
        void givenNonExistingMatch_whenUpdateMatch_thenThrows() {
            when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> matchService.updateMatch(matchId, matchDTO))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }


    @Nested
    @DisplayName("deleteMatch()")
    class DeleteMatchTests {

        @Test
        @DisplayName("Supprime un match SCHEDULED")
        void givenScheduledMatch_whenDelete_thenSuccess() {
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            doNothing().when(matchRepository).delete(match);

            matchService.deleteMatch(matchId);

            verify(matchRepository).delete(match);
        }

        @Test
        @DisplayName("Match en cours → impossible de supprimer")
        void givenInProgressMatch_whenDelete_thenThrows() {
            match.setStatus(StatusMatch.IN_PROGRESS);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.deleteMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Cannot delete");
        }

        @Test
        @DisplayName("Match complété → impossible de supprimer")
        void givenCompletedMatch_whenDelete_thenThrows() {
            match.setStatus(StatusMatch.COMPLETED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.deleteMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Cannot delete");
        }

        @Test
        @DisplayName("Match inexistant → ResourceNotFoundException")
        void givenNonExistingMatch_whenDelete_thenThrows() {
            when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> matchService.deleteMatch(matchId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }


    @Nested
    @DisplayName("cancelMatch()")
    class CancelMatchTests {

        @Test
        @DisplayName("L'admin peut annuler un match > 2h avant")
        void givenAdminAndEnoughTime_whenCancel_thenSuccess() {
            match.setTime(LocalDateTime.now().plusHours(3));
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            MatchDTO result = matchService.cancelMatch(matchId);

            assertThat(result).isNotNull();
            assertThat(match.getStatus()).isEqualTo(StatusMatch.CANCELLED);
            verify(notificationService, never())
                    .createNotification(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("L'organisateur créateur peut annuler son match")
        void givenCreatorOrganisateur_whenCancel_thenSuccess() {
            match.setCreatedBy(organizateurUser);
            match.setTime(LocalDateTime.now().plusHours(5));
            setupSecurityContext(organizateurUser);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            MatchDTO result = matchService.cancelMatch(matchId);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Annulation < 2h avant → BusinessException")
        void givenLessThan2Hours_whenCancel_thenThrows() {
            match.setTime(LocalDateTime.now().plusMinutes(90));
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.cancelMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("2 heurs");
        }

        @Test
        @DisplayName("Match complété → impossible d'annuler")
        void givenCompletedMatch_whenCancel_thenThrows() {
            match.setStatus(StatusMatch.COMPLETED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.cancelMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Cannot cancel a completed match");
        }

        @Test
        @DisplayName("Utilisateur non-créateur et non-admin → BusinessException")
        void givenNonCreatorNonAdmin_whenCancel_thenThrows() {
            setupSecurityContext(organizateurUser);
            // match.createdBy = adminUser (différent de organizateurUser)
            match.setTime(LocalDateTime.now().plusHours(5));
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.cancelMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("organisateur");
        }

        @Test
        @DisplayName("Notifications envoyées aux participants lors de l'annulation")
        void givenParticipants_whenCancel_thenNotificationsSent() {
            match.setTime(LocalDateTime.now().plusHours(4));

            User participant = new User();
            participant.setId(UUID.randomUUID());

            MatchParticipation participation = new MatchParticipation();
            participation.setUser(participant);
            match.getParticipations().add(participation);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            matchService.cancelMatch(matchId);

            verify(notificationService).createNotification(
                    eq(participant.getId()),
                    eq("Match annulé"),
                    anyString(),
                    eq(NotificationType.MATCH_CANCELLED),
                    eq(matchId)
            );
        }
    }

    @Nested
    @DisplayName("joinMatch() / leaveMatch()")
    class JoinLeaveMatchTests {

        @Test
        @DisplayName("Rejoindre un match disponible")
        void givenAvailableMatch_whenJoin_thenCurrentPlayersIncremented() {
            match.setCurrentPlayers(5);
            match.setPlayersNeeded(10);
            match.setStatus(StatusMatch.SCHEDULED);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            matchService.joinMatch(matchId);

            assertThat(match.getCurrentPlayers()).isEqualTo(6);
        }

        @Test
        @DisplayName("Match complet → impossible de rejoindre")
        void givenFullMatch_whenJoin_thenThrows() {
            match.setCurrentPlayers(10);
            match.setPlayersNeeded(10);
            match.setStatus(StatusMatch.SCHEDULED);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.joinMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Cannot join");
        }

        @Test
        @DisplayName("Quitter un match en cours de planification")
        void givenScheduledMatch_whenLeave_thenCurrentPlayersDecremented() {
            match.setCurrentPlayers(5);
            match.setStatus(StatusMatch.SCHEDULED);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            matchService.leaveMatch(matchId);

            assertThat(match.getCurrentPlayers()).isEqualTo(4);
        }

        @Test
        @DisplayName("Quitter un match avec 0 joueurs → BusinessException")
        void givenZeroPlayers_whenLeave_thenThrows() {
            match.setCurrentPlayers(0);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.leaveMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("No players to remove");
        }

        @Test
        @DisplayName("Quitter un match COMPLETED → BusinessException")
        void givenCompletedMatch_whenLeave_thenThrows() {
            match.setCurrentPlayers(5);
            match.setStatus(StatusMatch.COMPLETED);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.leaveMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("in progress or completed");
        }
    }


    @Nested
    @DisplayName("startMatch()")
    class StartMatchTests {

        @Test
        @DisplayName("Démarre un match SCHEDULED avec assez de joueurs")
        void givenScheduledMatchWithEnoughPlayers_whenStart_thenInProgress() {
            match.setStatus(StatusMatch.SCHEDULED);
            match.setCurrentPlayers(10);
            match.setPlayersNeeded(10);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            matchService.startMatch(matchId);

            assertThat(match.getStatus()).isEqualTo(StatusMatch.IN_PROGRESS);
        }

        @Test
        @DisplayName("Pas assez de joueurs → BusinessException")
        void givenNotEnoughPlayers_whenStart_thenThrows() {
            match.setStatus(StatusMatch.SCHEDULED);
            match.setCurrentPlayers(3);
            match.setPlayersNeeded(10);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.startMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("not enough players");
        }

        @Test
        @DisplayName("Match non-SCHEDULED → BusinessException")
        void givenNonScheduledMatch_whenStart_thenThrows() {
            match.setStatus(StatusMatch.CANCELLED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.startMatch(matchId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Only scheduled matches");
        }
    }


    @Nested
    @DisplayName("completeMatch()")
    class CompleteMatchTests {

        @Test
        @DisplayName("erminer un match IN_PROGRESS avec un gagnant valide")
        void givenInProgressMatch_whenComplete_thenCompleted() {
            match.setStatus(StatusMatch.IN_PROGRESS);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(teamRepository.findById(team1Id)).thenReturn(Optional.of(team1));
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            matchService.completeMatch(matchId, team1Id, 3, 1);

            assertThat(match.getStatus()).isEqualTo(StatusMatch.COMPLETED);
            assertThat(match.getScoreTeam1()).isEqualTo(3);
            assertThat(match.getScoreTeam2()).isEqualTo(1);
            assertThat(match.getWinnerTeam()).isEqualTo(team1);
        }

        @Test
        @DisplayName(" Match non IN_PROGRESS → BusinessException")
        void givenScheduledMatch_whenComplete_thenThrows() {
            match.setStatus(StatusMatch.SCHEDULED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.completeMatch(matchId, team1Id, 2, 0))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("in progress");
        }

        @Test
        @DisplayName("Équipe gagnante non participante → BusinessException")
        void givenExternalWinner_whenComplete_thenThrows() {
            match.setStatus(StatusMatch.IN_PROGRESS);
            UUID externalTeamId = UUID.randomUUID();

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.completeMatch(matchId, externalTeamId, 2, 1))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Winner team must be");
        }
    }



    @Nested
    @DisplayName("postponeMatch()")
    class PostponeMatchTests {

        @Test
        @DisplayName("Reporte un match avec une nouvelle heure valide")
        void givenValidNewTime_whenPostpone_thenPostponed() {
            LocalDateTime newTime = LocalDateTime.now().plusDays(5);
            match.setStatus(StatusMatch.SCHEDULED);

            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
            when(matchRepository.existsDuplicateMatch(any(), any(), any(), any())).thenReturn(false);
            when(matchRepository.findByTerrainAndDateRange(any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(matchRepository.save(match)).thenReturn(match);
            when(matchMapper.toDTO(match)).thenReturn(matchDTO);

            matchService.postponeMatch(matchId, newTime);

            assertThat(match.getStatus()).isEqualTo(StatusMatch.POSTPONED);
            assertThat(match.getTime()).isEqualTo(newTime);
        }

        @Test
        @DisplayName("Reporter un match COMPLETED → BusinessException")
        void givenCompletedMatch_whenPostpone_thenThrows() {
            match.setStatus(StatusMatch.COMPLETED);
            when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

            assertThatThrownBy(() -> matchService.postponeMatch(matchId, LocalDateTime.now().plusDays(3)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Cannot postpone a completed match");
        }
    }


    @Nested
    @DisplayName("Filtres & listes")
    class FilterTests {

        @Test
        @DisplayName("getMatchesByStatus() délègue au repository")
        void whenGetMatchesByStatus_thenDelegateToRepository() {
            when(matchRepository.findByStatus(StatusMatch.SCHEDULED)).thenReturn(List.of(match));
            when(matchMapper.toDTOList(any())).thenReturn(List.of(matchDTO));

            List<MatchDTO> result = matchService.getMatchesByStatus(StatusMatch.SCHEDULED);

            assertThat(result).hasSize(1);
            verify(matchRepository).findByStatus(StatusMatch.SCHEDULED);
        }

        @Test
        @DisplayName("getUpcomingMatches() retourne les matchs à venir")
        void whenGetUpcomingMatches_thenReturnFutureMatches() {
            when(matchRepository.findUpcomingMatches(any())).thenReturn(List.of(match));
            when(matchMapper.toDTOList(any())).thenReturn(List.of(matchDTO));

            List<MatchDTO> result = matchService.getUpcomingMatches();

            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("getTodayMatches() utilise la plage du jour")
        void whenGetTodayMatches_thenCallFindByTimeBetween() {
            when(matchRepository.findByTimeBetween(any(), any())).thenReturn(List.of(match));
            when(matchMapper.toDTOList(any())).thenReturn(List.of(matchDTO));

            List<MatchDTO> result = matchService.getTodayMatches();

            assertThat(result).hasSize(1);
            verify(matchRepository).findByTimeBetween(any(), any());
        }

        @Test
        @DisplayName("getMatchesBetweenDates() avec start > end → BusinessException")
        void givenStartAfterEnd_whenGetBetweenDates_thenThrows() {
            LocalDateTime start = LocalDateTime.now().plusDays(5);
            LocalDateTime end   = LocalDateTime.now().plusDays(1);

            assertThatThrownBy(() -> matchService.getMatchesBetweenDates(start, end))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Start date must be before end date");
        }

        @Test
        @DisplayName("getMatchesByTerrain() → terrain existant")
        void givenExistingTerrain_whenGetMatchesByTerrain_thenReturnList() {
            when(terrainRepository.existsById(terrainId)).thenReturn(true);
            when(matchRepository.findByTerrainId(terrainId)).thenReturn(List.of(match));
            when(matchMapper.toDTOList(any())).thenReturn(List.of(matchDTO));

            List<MatchDTO> result = matchService.getMatchesByTerrain(terrainId);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("getMatchesByTerrain() → terrain inexistant → ResourceNotFoundException")
        void givenNonExistingTerrain_whenGetMatchesByTerrain_thenThrows() {
            when(terrainRepository.existsById(terrainId)).thenReturn(false);

            assertThatThrownBy(() -> matchService.getMatchesByTerrain(terrainId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("getMatchesByTeam() → équipe existante")
        void givenExistingTeam_whenGetMatchesByTeam_thenReturnList() {
            when(teamRepository.existsById(team1Id)).thenReturn(true);
            when(matchRepository.findAllByTeamId(team1Id)).thenReturn(List.of(match));
            when(matchMapper.toDTOList(any())).thenReturn(List.of(matchDTO));

            List<MatchDTO> result = matchService.getMatchesByTeam(team1Id);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("searchMatches() délègue au repository")
        void whenSearchMatches_thenDelegateToRepository() {
            when(matchRepository.searchByKeyword("derby")).thenReturn(List.of(match));
            when(matchMapper.toDTOList(any())).thenReturn(List.of(matchDTO));

            List<MatchDTO> result = matchService.searchMatches("derby");

            assertThat(result).hasSize(1);
            verify(matchRepository).searchByKeyword("derby");
        }

        @Test
        @DisplayName("getFullMatches() retourne uniquement les matchs complets")
        void whenGetFullMatches_thenReturnOnlyFullMatches() {
            match.setCurrentPlayers(10);
            match.setPlayersNeeded(10);

            Match notFullMatch = Match.builder()
                    .id(UUID.randomUUID())
                    .currentPlayers(5)
                    .playersNeeded(10)
                    .participations(new ArrayList<>())
                    .build();

            when(matchRepository.findAll()).thenReturn(List.of(match, notFullMatch));
            when(matchMapper.toDTOList(argThat(list -> list.size() == 1))).thenReturn(List.of(matchDTO));

            List<MatchDTO> result = matchService.getFullMatches();

            assertThat(result).hasSize(1);
        }
    }


    @Nested
    @DisplayName("getMatchStatistics()")
    class MatchStatisticsTests {

        @Test
        @DisplayName("Retourne des statistiques correctes")
        void whenGetStatistics_thenReturnCorrectCounts() {
            when(matchRepository.count()).thenReturn(10L);
            when(matchRepository.countByStatus(StatusMatch.SCHEDULED)).thenReturn(4L);
            when(matchRepository.countByStatus(StatusMatch.IN_PROGRESS)).thenReturn(1L);
            when(matchRepository.countByStatus(StatusMatch.COMPLETED)).thenReturn(3L);
            when(matchRepository.countByStatus(StatusMatch.CANCELLED)).thenReturn(1L);
            when(matchRepository.countByStatus(StatusMatch.POSTPONED)).thenReturn(1L);
            when(matchRepository.findUpcomingMatches(any())).thenReturn(List.of(match));
            when(matchRepository.findByStatus(StatusMatch.COMPLETED)).thenReturn(Collections.emptyList());

            MatchService.MatchStatistics stats = matchService.getMatchStatistics();

            assertThat(stats.getTotalMatches()).isEqualTo(10L);
            assertThat(stats.getScheduled()).isEqualTo(4L);
            assertThat(stats.getCompleted()).isEqualTo(3L);
            assertThat(stats.getCancelled()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Moyenne des joueurs calculée sur les matchs à venir")
        void whenGetStatistics_thenAveragePlayersCalculated() {
            match.setCurrentPlayers(8);
            when(matchRepository.count()).thenReturn(1L);
            when(matchRepository.countByStatus(any())).thenReturn(0L);
            when(matchRepository.findUpcomingMatches(any())).thenReturn(List.of(match));
            when(matchRepository.findByStatus(StatusMatch.COMPLETED)).thenReturn(Collections.emptyList());

            MatchService.MatchStatistics stats = matchService.getMatchStatistics();

            assertThat(stats.getAveragePlayersPerMatch()).isEqualTo(8.0);
        }
    }

    @Nested
    @DisplayName("isTerrainAvailable()")
    class IsTerrainAvailableTests {

        @Test
        @DisplayName("Retourne true quand le terrain est libre")
        void givenFreeTerrain_whenCheck_thenReturnTrue() {
            when(matchRepository.isTerrainBusy(eq(terrainId), any())).thenReturn(false);

            boolean result = matchService.isTerrainAvailable(terrainId, LocalDateTime.now().plusDays(1), null);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Retourne false quand le terrain est occupé")
        void givenBusyTerrain_whenCheck_thenReturnFalse() {
            when(matchRepository.isTerrainBusy(eq(terrainId), any())).thenReturn(true);

            boolean result = matchService.isTerrainAvailable(terrainId, LocalDateTime.now().plusDays(1), null);

            assertThat(result).isFalse();
        }
    }


    @Nested
    @DisplayName("Compteurs")
    class CounterTests {

        @Test
        @DisplayName("countMatchesByStatus() délègue au repository")
        void whenCountByStatus_thenDelegate() {
            when(matchRepository.countByStatus(StatusMatch.COMPLETED)).thenReturn(5L);

            long count = matchService.countMatchesByStatus(StatusMatch.COMPLETED);

            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("countMatchesByTerrain() → terrain existant")
        void givenExistingTerrain_whenCountByTerrain_thenReturnCount() {
            when(terrainRepository.existsById(terrainId)).thenReturn(true);
            when(matchRepository.countByTerrainId(terrainId)).thenReturn(3L);

            long count = matchService.countMatchesByTerrain(terrainId);

            assertThat(count).isEqualTo(3L);
        }

        @Test
        @DisplayName("countMatchesByTerrain() → terrain inexistant → ResourceNotFoundException")
        void givenNonExistingTerrain_whenCountByTerrain_thenThrows() {
            when(terrainRepository.existsById(terrainId)).thenReturn(false);

            assertThatThrownBy(() -> matchService.countMatchesByTerrain(terrainId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getHeadToHead()")
    class HeadToHeadTests {

        @Test
        @DisplayName("Retourne les matchs entre deux équipes, triés par date décroissante")
        void givenTwoTeams_whenGetHeadToHead_thenReturnSortedMatches() {
            Match older = Match.builder()
                    .id(UUID.randomUUID())
                    .team1(team1).team2(team2)
                    .time(LocalDateTime.now().minusDays(10))
                    .participations(new ArrayList<>())
                    .build();
            Match newer = Match.builder()
                    .id(UUID.randomUUID())
                    .team1(team2).team2(team1)
                    .time(LocalDateTime.now().minusDays(1))
                    .participations(new ArrayList<>())
                    .build();

            when(teamRepository.existsById(team1Id)).thenReturn(true);
            when(teamRepository.existsById(team2Id)).thenReturn(true);
            when(matchRepository.findAll()).thenReturn(List.of(older, newer, match));
            when(matchMapper.toDTOList(anyList())).thenReturn(List.of(matchDTO, matchDTO));

            List<MatchDTO> result = matchService.getHeadToHead(team1Id, team2Id);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Équipe inexistante → ResourceNotFoundException")
        void givenNonExistingTeam_whenGetHeadToHead_thenThrows() {
            when(teamRepository.existsById(team1Id)).thenReturn(false);

            assertThatThrownBy(() -> matchService.getHeadToHead(team1Id, team2Id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");
        }
    }
}