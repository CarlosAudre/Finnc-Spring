package com.project.FinnC.user;

import com.project.FinnC.auth.RegisterService;
import com.project.FinnC.exeptions.EmailAlreadyExistsException;
import com.project.FinnC.exeptions.InvalidPasswordException;
import com.project.FinnC.infra_security.MessageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    RegisterService registerService;

    public UserNameDto changeUserName(UserNameDto dto, User user){
        user.setName(dto.name());
        userRepository.save(user);
        return new UserNameDto(dto.name());
    }

    public EmailResponseDto changeUserEmail(UserEmailDto dto, User user){
        if(userRepository.findByEmail(dto.email()) != null){
            throw new EmailAlreadyExistsException();
        }
        if(!passwordEncoder.matches(dto.password(), user.getPassword())){
            throw new InvalidPasswordException();
        }
        user.setEmail(dto.email());
        userRepository.save(user);
        return new EmailResponseDto(dto.email());
    }

    public UserPhotoDto changeUserPhoto(UserPhotoDto dto, User user){
        user.setImgUrl(dto.photo());
        userRepository.save(user);
        return new UserPhotoDto(dto.photo());
    };

    public MessageResponseDto changeUserPassword(UserPasswordDto dto, User user){
        if(!passwordEncoder.matches(dto.currentPassword(), user.getPassword())){
            throw new InvalidPasswordException("Senha atual incorreta");
        }
        if(!registerService.passwordValidation(dto.newPassword())){
            throw new InvalidPasswordException("A nova senha digitada não é segura o suficiente");
        }
        String encryptedPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);

        return new MessageResponseDto("Senha alterada com sucesso");
    }


}
