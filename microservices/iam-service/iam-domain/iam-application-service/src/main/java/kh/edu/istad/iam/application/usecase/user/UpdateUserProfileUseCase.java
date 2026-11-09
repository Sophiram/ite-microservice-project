package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.command.UpdateUserProfileCommand;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.application.result.UserResult;
import kh.edu.istad.iam.application.usecase.UseCaseSupport;
import kh.edu.istad.iam.application.usecase.user.UserUseCaseSupport;
import kh.edu.istad.iam.domain.entity.User;

import static kh.edu.istad.iam.application.usecase.user.UserUseCaseSupport.merge;

public final class UpdateUserProfileUseCase {
        private final UserRepository users;

        public UpdateUserProfileUseCase(UserRepository users) { this.users = users; }

        public UserResult execute(UpdateUserProfileCommand command) {
            User user = UseCaseSupport.require(users.findByUuid(command.userUuid()), "User");
            user.updateProfile(UserUseCaseSupport.merge(user.getProfile(), command.profile()));
            if (command.address() != null) user.updateAddress(UserUseCaseSupport.merge(user.getAddress(), command.address()));
            return UserResult.from(users.save(user));
        }
    }
