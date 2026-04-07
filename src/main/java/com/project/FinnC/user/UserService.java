package com.project.FinnC.user;

import com.project.FinnC.auth.RegisterService;
import com.project.FinnC.exeptions.EmailAlreadyExistsException;
import com.project.FinnC.exeptions.InvalidPasswordException;
import com.project.FinnC.infra_security.MessageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.io.IOException;
import java.util.UUID;

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

    public String changeUserPhoto(MultipartFile file, User user, String baseUrl) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        String imgUrl = baseUrl + "/uploads/" + fileName;

        user.setImgUrl(imgUrl);
        userRepository.save(user);

        return imgUrl;
    }

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
