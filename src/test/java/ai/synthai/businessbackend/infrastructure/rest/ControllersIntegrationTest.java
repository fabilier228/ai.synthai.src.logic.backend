package ai.synthai.businessbackend.infrastructure.rest;

import ai.synthai.businessbackend.application.dto.TranscriptionResponseDto;
import ai.synthai.businessbackend.application.service.AudiobookTranscriptionService;
import ai.synthai.businessbackend.application.service.ConversationTranscriptionService;
import ai.synthai.businessbackend.application.service.LanguageDetectionService;
import ai.synthai.businessbackend.application.service.LectureTranscriptionService;
import ai.synthai.businessbackend.application.service.SongTranscriptionService;
import ai.synthai.businessbackend.application.service.TranscriptionFileService;
import ai.synthai.businessbackend.domain.model.Category;
import ai.synthai.businessbackend.domain.model.Language;
import ai.synthai.businessbackend.domain.model.Status;
import ai.synthai.businessbackend.domain.port.outbound.TranscriptionRespositoryPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        TranscriptionController.class,
        LanguageDetectionController.class,
        AnalysisController.class
})
class ControllersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TranscriptionRespositoryPort transcriptionRespositoryPort;

    @MockBean
    private TranscriptionFileService transcriptionFileService;

    @MockBean
    private LanguageDetectionService languageDetectionService;

    @MockBean
    private SongTranscriptionService songTranscriptionService;

    @MockBean
    private LectureTranscriptionService lectureTranscriptionService;

    @MockBean
    private ConversationTranscriptionService conversationTranscriptionService;

    @MockBean
    private AudiobookTranscriptionService audiobookTranscriptionService;

    @Test
    void shouldReturnUserTranscriptions() throws Exception {
        when(transcriptionRespositoryPort.findByKeycloakId("user-1"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/transcriptions/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.transcriptions").isArray());
    }

    @Test
    void shouldDetectLanguageFromUploadedFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "audioFile",
                "sample.wav",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "fake-audio".getBytes()
        );

        when(languageDetectionService.detectLanguage(ArgumentMatchers.any()))
                .thenReturn(Language.ENGLISH);

        mockMvc.perform(multipart("/detect-language").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("ENGLISH")));
    }

    @Test
    void shouldAnalyzeSongRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "audioFile",
                "track.wav",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "fake-audio".getBytes()
        );

        TranscriptionResponseDto<Object> response = TranscriptionResponseDto.builder()
                .status(Status.SUCCESS)
                .category(Category.SONG)
                .language(Language.ENGLISH)
                .duration(1)
                .transcriptionAnalysis(null)
                .build();

        when(songTranscriptionService.analyzeSong(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(Language.ENGLISH),
                ArgumentMatchers.eq("kc-1"),
                ArgumentMatchers.eq("sample-title"),
                ArgumentMatchers.eq(0.0),
                ArgumentMatchers.eq(false),
                ArgumentMatchers.anyList()))
                .thenReturn(response);

        mockMvc.perform(multipart("/analysis/song")
                        .file(file)
                        .param("language", "ENGLISH")
                        .param("keycloakId", "kc-1")
                        .param("title", "sample-title")
                        .param("temperature", "0.0")
                        .param("diarization", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.category").value("SONG"))
                .andExpect(jsonPath("$.language").value("ENGLISH"));
    }

    @Test
    void shouldDeleteTranscriptionById() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/transcriptions/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Transcription deleted successfully."));
    }

    @Test
    void shouldReturnSingleTranscriptionPayload() throws Exception {
        when(transcriptionRespositoryPort.findById(55L)).thenReturn(null);

        mockMvc.perform(get("/transcriptions/55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void shouldAnalyzeConversationRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "audioFile",
                "conversation.wav",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "fake-audio".getBytes()
        );

        TranscriptionResponseDto<Object> response = TranscriptionResponseDto.builder()
                .status(Status.SUCCESS)
                .category(Category.CONVERSATION)
                .language(Language.ENGLISH)
                .duration(1)
                .transcriptionAnalysis(null)
                .build();

        when(conversationTranscriptionService.analyzeConversation(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(Language.ENGLISH),
                ArgumentMatchers.eq("kc-2"),
                ArgumentMatchers.eq("conversation-title"),
                ArgumentMatchers.eq(0.2),
                ArgumentMatchers.eq(false),
                ArgumentMatchers.anyList()))
                .thenReturn(response);

        mockMvc.perform(multipart("/analysis/conversation")
                        .file(file)
                        .param("language", "ENGLISH")
                        .param("keycloakId", "kc-2")
                        .param("title", "conversation-title")
                        .param("temperature", "0.2")
                        .param("diarization", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.category").value("CONVERSATION"));
    }

    @Test
    void shouldReturnFailureWhenSongAnalysisThrowsException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "audioFile",
                "broken.wav",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "fake-audio".getBytes()
        );

        when(songTranscriptionService.analyzeSong(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(Language.ENGLISH),
                ArgumentMatchers.eq("kc-3"),
                ArgumentMatchers.eq("broken-title"),
                ArgumentMatchers.eq(0.0),
                ArgumentMatchers.eq(false),
                ArgumentMatchers.anyList()))
                .thenThrow(new RuntimeException("simulated failure"));

        mockMvc.perform(multipart("/analysis/song")
                        .file(file)
                        .param("language", "ENGLISH")
                        .param("keycloakId", "kc-3")
                        .param("title", "broken-title")
                        .param("temperature", "0.0")
                        .param("diarization", "false"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.category").value("SONG"));
    }
}
