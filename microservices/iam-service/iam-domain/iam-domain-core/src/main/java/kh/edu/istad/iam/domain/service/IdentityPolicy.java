package kh.edu.istad.iam.domain.service;

import java.util.Collection;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.Provider;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.exception.DomainException;
import kh.edu.istad.iam.domain.exception.ErrorCode;

/** Rules for linking / unlinking social identities. */
public final class IdentityPolicy {

    private IdentityPolicy() { }

    /** One identity per provider per user. */
    public static void assertCanLink(Collection<Identity> activeIdentities, Provider provider) {
        boolean exists = activeIdentities.stream()
                .anyMatch(i -> !i.isDeleted() && i.getProvider() == provider);
        if (exists) {
            throw new DomainException(ErrorCode.IDENTITY_ALREADY_LINKED,
                    "User already has a " + provider + " identity linked");
        }
    }

    /** A user cannot remove their last sign-in method (password or a linked identity). */
    public static void assertCanUnlink(User user, Collection<Identity> activeIdentities) {
        long identities = activeIdentities.stream().filter(i -> !i.isDeleted()).count();
        long methods = identities + (user.hasPassword() ? 1 : 0);
        if (methods <= 1) {
            throw new DomainException(ErrorCode.LAST_SIGN_IN_METHOD,
                    "Cannot unlink the last sign-in method");
        }
    }
}
