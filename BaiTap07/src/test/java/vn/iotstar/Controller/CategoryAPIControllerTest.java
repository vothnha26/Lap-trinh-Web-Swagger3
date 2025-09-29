package vn.iotstar.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureWebMvc
public class CategoryAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetCategoryById() throws Exception {
        mockMvc.perform(get("/api/category/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateCategory() throws Exception {
        mockMvc.perform(post("/api/category")
                .param("categoryName", "Test Category")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}
