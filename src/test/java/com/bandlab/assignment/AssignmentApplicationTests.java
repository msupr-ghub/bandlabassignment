package com.bandlab.assignment;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
class AssignmentApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	private void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void createPost_withValidImage_shouldReturnOk() throws Exception {
		MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new FileInputStream("src/test/resources/test.jpg"));
		String caption = "Sample caption";
		Long userId = 1L;
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:" + port + "/api/v1/posts")
						.file(imageFile)
						.param("caption", caption)
						.param("userId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.caption").value(caption))
				.andExpect(jsonPath("$.userId").value(userId)).andReturn();
		String responseContent = response.getResponse().getContentAsString();
		Long postId = JsonPath.parse(responseContent).read("$.id", Long.class);
		assertThat(postId).isNotNull();

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/api/v1/posts/" + postId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.caption").value(caption))
				.andExpect(jsonPath("$.userId").value(userId));

		mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "/api/v1/posts/" + postId))
				.andExpect(status().isOk());

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/api/v1/posts/" + postId))
				.andExpect(status().isNotFound());

	}

	@Test
	public void createPost_withUnsupportedImageType_shouldReturnBadRequest() throws Exception {

		MockMultipartFile imageFile = new MockMultipartFile("image", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes());
		String caption = "Sample caption";
		Long userId = 1L;

		mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:" + port + "/api/v1/posts")
						.file(imageFile)
						.param("caption", caption)
						.param("userId", userId.toString()))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void addComment_withValidDetails_shouldReturnOk() throws Exception {

		MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new FileInputStream("src/test/resources/test.jpg"));
		String caption = "Sample caption";
		Long userId = 1L;
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:" + port + "/api/v1/posts")
						.file(imageFile)
						.param("caption", caption)
						.param("userId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.caption").value(caption))
				.andExpect(jsonPath("$.userId").value(userId)).andReturn();

		String comment = "Sample comment";
		String postId = JsonPath.parse(response.getResponse().getContentAsString()).read("$.id", String.class);

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + comment + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/api/v1/posts/" + postId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.comments[0].content").value(comment));

	}

	@Test
	public void addComment_withInvalidPostId_shouldReturnBadRequest() throws Exception {

		MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new FileInputStream("src/test/resources/test.jpg"));
		String caption = "Sample caption";
		Long userId = 1L;
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:" + port + "/api/v1/posts")
						.file(imageFile)
						.param("caption", caption)
						.param("userId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.caption").value(caption))
				.andExpect(jsonPath("$.userId").value(userId)).andReturn();
		Long postId = JsonPath.parse(response.getResponse().getContentAsString()).read("$.id", Long.class);

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId +"/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"Sample comment\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void listPosts_withComments_ShouldReturnTwoCommentsPerPostOnly() throws Exception {
		MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new FileInputStream("src/test/resources/test.jpg"));
		String caption = "Sample caption";
		Long userId = 1L;

		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:" + port + "/api/v1/posts")
						.file(imageFile)
						.param("caption", caption)
						.param("userId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.caption").value(caption))
				.andExpect(jsonPath("$.userId").value(userId)).andReturn();
		Long postId = JsonPath.parse(response.getResponse().getContentAsString()).read("$.id", Long.class);

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 1" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 2" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 3" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();

		// comment 3 and 2 should be the only comments available in the LIST API response for the post
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/api/v1/posts"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].comments.length()").value(2))
				.andExpect(jsonPath("$[0].comments[0].content").value("comment 2"))
				.andExpect(jsonPath("$[0].comments[1].content").value("comment 3"));


	}

	@Test
	public void deleteComment_ShouldDeleteCommentFromThePost() throws Exception {

		MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new FileInputStream("src/test/resources/test.jpg"));
		String caption = "Sample caption";
		Long userId = 1L;

		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:" + port + "/api/v1/posts")
						.file(imageFile)
						.param("caption", caption)
						.param("userId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.caption").value(caption))
				.andExpect(jsonPath("$.userId").value(userId)).andReturn();
		Long postId = JsonPath.parse(response.getResponse().getContentAsString()).read("$.id", Long.class);

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 1" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 2" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();
		response = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 3" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();

		Long commentId = JsonPath.parse(response.getResponse().getContentAsString()).read("$.entity.id", Long.class);
		mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments/" + commentId))
				.andExpect(status().isOk());
		// try to get the deleted comment to verify that comment has been deleted
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments/" + commentId))
				.andExpect(status().isNotFound());
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/api/v1/posts/" + postId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.comments.length()").value(2));

	}

	@Test
	public void deletePost_shouldAlsoDeletedAssociatedCommentsFromDB() throws Exception {
		// create a post
		MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, new FileInputStream("src/test/resources/test.jpg"));
		String caption = "Sample caption";
		Long userId = 1L;
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.multipart("http://localhost:" + port + "/api/v1/posts")
						.file(imageFile)
						.param("caption", caption)
						.param("userId", userId.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.caption").value(caption))
				.andExpect(jsonPath("$.userId").value(userId)).andReturn();

		Long postId = JsonPath.parse(response.getResponse().getContentAsString()).read("$.id", Long.class);

		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 1" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();
		mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 2" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();
		response = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"content\": \"" + "comment 3" + "\", \"creator\": 1}"))
				.andExpect(status().isOk()).andReturn();

		Long latestComment = JsonPath.parse(response.getResponse().getContentAsString()).read("$.entity.id", Long.class);
		mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:" + port + "/api/v1/posts/" + postId))
				.andExpect(status().isOk());
		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:" + port + "/api/v1/posts/" + postId + "/comments/" + latestComment))
				.andExpect(status().isNotFound());

	}

}
