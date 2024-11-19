package com.example.demo;

import com.example.demo.base.NodeEntity;
import com.example.demo.base.Question;
import com.example.demo.questions.ChildQuestion;
import com.example.demo.questions.IbanQuestion;
import com.example.demo.questions.WorkAbilityQuestion;
import com.example.demo.questions.insurance.DisabilityInsuranceQuestion;
import com.example.demo.repository.AleAntragRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class AleAntragIntegrationTest {

//    @Container
//    public static PostgreSQLContainer<?> postgreSQLContainer =
//            new PostgreSQLContainer<>("postgres:15-alpine")
//                    .withDatabaseName("test_db")
//                    .withUsername("test")
//                    .withPassword("test");
//
//    static {
//        postgreSQLContainer.start();
//        System.setProperty("DB_URL", postgreSQLContainer.getJdbcUrl());
//        System.setProperty("DB_USERNAME", postgreSQLContainer.getUsername());
//        System.setProperty("DB_PASSWORD", postgreSQLContainer.getPassword());
//    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AleAntragRepository aleAntragRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Test case: Create a new ALE Antrag by calling the POST endpoint with mockMvc
    @Transactional
    @Test
    public void createAleAntrag() throws Exception {
        // Given
        // When
        mockMvc.perform(post("/api/ale_antrag"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    result.getResponse().setCharacterEncoding("UTF-8");
                    String contentAsString = result.getResponse().getContentAsString();
                    System.out.println(contentAsString);
                    AleAntrag returnedAntrag = (AleAntrag) objectMapper.readValue(contentAsString, NodeEntity.class);

                    assertThat(returnedAntrag.getMetadaten().getOwnerUserId()).isEqualTo(TestData.ownerUserId);
                    assertThat(returnedAntrag.getMetadaten().getStatus()).isEqualTo("DRAFT");

                    List<Question<?>> childNodes = returnedAntrag.getChildNodes();

                    // assert that all questions are present
                    assertThat(childNodes).hasSize(4);
                    // assert that at least one child node is of each type
                    assertThat(childNodes).extracting("class").contains(IbanQuestion.class, WorkAbilityQuestion.class, ChildQuestion.class, DisabilityInsuranceQuestion.class);

                    // check db
                    AleAntrag aleAntrag = aleAntragRepository.findById(returnedAntrag.getId()).orElseThrow();
                    assertThat(aleAntrag).isNotNull();
                });


        // Then
    }

}
