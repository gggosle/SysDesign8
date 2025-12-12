package com.sysdesign.banking.dto;

import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LockRequest {
    private boolean lock;
    private Integer userId;
}