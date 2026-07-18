package com.project.FinnC.ai;

public record MessageDTO(MessageRequestDTO message, String userName, Long userId, String authorization) {
}
