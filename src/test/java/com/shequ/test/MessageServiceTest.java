package com.shequ.test;

import com.shequ.dto.MessageDTO;
import com.shequ.entity.SystemMessage;
import com.shequ.repository.SystemMessageRepository;
import com.shequ.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private SystemMessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void getMessageById_Success() {
        // 准备测试数据
        Long messageId = 1L;
        SystemMessage mockMessage = new SystemMessage();
        mockMessage.setId(messageId);

        // 设置mock行为
        doReturn(Optional.of(mockMessage)).when(messageRepository).findById(messageId);

        // 执行测试
        MessageDTO result = messageService.getMessageById(messageId);

        // 验证结果
        assertNotNull(result);
        assertEquals(messageId, result.getId());
    }

    @Test
    void getMessageById_NotFound() {
        // 准备测试数据
        Long nonExistId = 999L;

        // 设置mock行为返回空
        doReturn(Optional.empty()).when(messageRepository).findById(nonExistId);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> messageService.getMessageById(nonExistId));

        assertEquals("消息不存在", exception.getMessage());
    }
}