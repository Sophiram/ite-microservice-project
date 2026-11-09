package kh.edu.istad.iam.application.usecase.identity;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;
import kh.edu.istad.iam.application.command.FederatedIdentityCommand;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.AuthUserResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.application.usecase.user.LoadUserForAuthenticationUseCase;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.Profile;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.domain.service.IdentityPolicy;

public final class FindOrProvisionFederatedUserUseCase {
        private final UserRepository users;
        private final IdentityRepository identities;
        private final CurrentActorProvider actors;
        private final LoadUserForAuthenticationUseCase loadForAuthentication;
        private final RoleRepository roles;
        private final AssignmentRepository assignments;
        private final Clock clock;

        public FindOrProvisionFederatedUserUseCase(
                UserRepository users, IdentityRepository identities, CurrentActorProvider actors,
                LoadUserForAuthenticationUseCase loadForAuthentication, Clock clock) {
            this(users, identities, actors, loadForAuthentication, null, null, clock);
        }

        public FindOrProvisionFederatedUserUseCase(
                UserRepository users, IdentityRepository identities, CurrentActorProvider actors,
                LoadUserForAuthenticationUseCase loadForAuthentication,
                RoleRepository roles, AssignmentRepository assignments, Clock clock) {
            this.users = users;
            this.identities = identities;
            this.actors = actors;
            this.loadForAuthentication = loadForAuthentication;
            this.roles = roles;
            this.assignments = assignments;
            this.clock = clock;
        }

        public AuthUserResult execute(FederatedIdentityCommand command) {
            var existingIdentity = identities.findByProviderAndSubject(command.provider(), command.subject());
            if (existingIdentity.isPresent()) {
                User existingUser = UseCaseSupport.require(
                        users.findById(existingIdentity.get().getUserId()), "User");
                existingIdentity.get().recordLogin(clock.instant());
                identities.save(existingIdentity.get());

                Profile cur = existingUser.getProfile();
                String given = cur != null && cur.givenName() != null && !cur.givenName().isBlank()
                        ? cur.givenName() : attribute(command.attributes(), "given_name");
                String fam = cur != null && cur.familyName() != null && !cur.familyName().isBlank()
                        ? cur.familyName() : attribute(command.attributes(), "family_name");
                String pic = cur != null && cur.pictureUrl() != null && !cur.pictureUrl().isBlank()
                        ? cur.pictureUrl() : command.pictureUrl();
                if (cur != null && (!java.util.Objects.equals(pic, cur.pictureUrl())
                        || !java.util.Objects.equals(fam, cur.familyName())
                        || !java.util.Objects.equals(given, cur.givenName()))) {
                    existingUser.updateProfile(new Profile(
                            given, fam, cur.middleName(), cur.nickname(), cur.gender(),
                            cur.birthdate(), cur.profileUrl(), pic, cur.websiteUrl(),
                            cur.zoneinfo(), cur.locale(), cur.phoneNumber()));
                    users.save(existingUser);
                }

                return loadForAuthentication.execute(existingUser.getUsername());
            }

            User user = command.emailVerified() && command.email() != null
                    ? users.findByEmail(User.normalizeEmail(command.email())).orElse(null)
                    : null;
            if (user == null) {
                String username = uniqueUsername(command.username(), command.provider().registrationId());
                String email = command.emailVerified() && command.email() != null
                        ? User.normalizeEmail(command.email())
                        : "federated-" + UUID.randomUUID() + "@users.invalid";
                var attributes = command.attributes();
                String givenName = attribute(attributes, "given_name");
                String familyName = attribute(attributes, "family_name");
                if (givenName == null || givenName.isBlank()) {
                    givenName = command.displayName() == null || command.displayName().isBlank()
                            ? username : command.displayName().trim();
                }
                Profile profile = new Profile(givenName, familyName, attribute(attributes, "middle_name"),
                        attribute(attributes, "nickname"), attribute(attributes, "gender"),
                        parseDate(attribute(attributes, "birthdate")), attribute(attributes, "profile"),
                        command.pictureUrl(), attribute(attributes, "website"), attribute(attributes, "zoneinfo"),
                        defaultValue(attribute(attributes, "locale"), "en"),
                        attribute(attributes, "phone_number"));
                user = User.provisionFederated(username, email, command.emailVerified(), profile);
                user.setPhoneNumberVerified(Boolean.TRUE.equals(attributes.get("phone_number_verified")));
                user = users.save(user);
                assignDefaultRole(user);
            }

            IdentityPolicy.assertCanLink(identities.findByUserId(user.getId()), command.provider());
            Identity identity = Identity.link(user.getId(), command.provider(), command.subject(), command.email(),
                    command.emailVerified(), command.displayName(), command.username(), command.pictureUrl(),
                    command.attributes(), clock.instant());
            identity.setCreatedBy(actors.currentActor());
            identities.save(identity);
            return loadForAuthentication.execute(user.getUsername());
        }

        private String uniqueUsername(String requestedUsername, String fallback) {
            String base = requestedUsername == null || requestedUsername.isBlank()
                    ? fallback : requestedUsername.trim();
            String candidate = base;
            while (users.existsByUsername(candidate)) {
                candidate = base + "-" + UUID.randomUUID().toString().substring(0, 8);
            }
            return candidate;
        }

        private void assignDefaultRole(User user) {
            if (roles == null || assignments == null) return;
            roles.findByName("USER").ifPresent(role -> assignments.save(UserRole.assign(user.getId(), role,
                    actors.currentActor(), null, "Default role for federated sign-in", clock.instant())));
        }

        private static String attribute(java.util.Map<String, Object> attributes, String name) {
            Object value = attributes.get(name);
            return value == null || value.toString().isBlank() ? null : value.toString();
        }

        private static LocalDate parseDate(String value) {
            if (value == null) return null;
            try { return LocalDate.parse(value); }
            catch (java.time.format.DateTimeParseException ignored) { return null; }
        }

        private static String defaultValue(String value, String fallback) {
            return value == null || value.isBlank() ? fallback : value;
        }
    }
