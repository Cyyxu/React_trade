// package com.xyex.controller;

// import java.util.List;

// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.xyex.entity.model.Post;
// import com.xyex.service.PostService;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import io.swagger.v3.oas.annotations.parameters.RequestBody;
// import io.swagger.v3.oas.annotations.tags.Tag;

// @RestController
// @RequestMapping("/post")
// @Tag(name = "帖子管理")
// @RequiredArgsConstructor
// @Slf4j
// public class PostController {

//     private final PostService postService;
//     /**
//      * 发表帖子
//      */
//     @PostMapping("/add")
//     public void addPost(@RequestBody Post post) {
//         postService.addPost(post);
//     }
//     /**
//      * 删除帖子
//      */
//     @PostMapping("/delete")
//     public void deletePost(@RequestBody List<Long> ids) {
//         postService.deletePost(ids);
//     }
//     /**
//      * 更新帖子
//      */
//     @PostMapping("/update")
//     public void updatePost(@RequestBody Post post) {
//         postService.updatePost(post);
//     }
//     /**
//      * 查询帖子
//      */
//     @PostMapping("/list")
//     public void listPost(@RequestBody Post post) {
//         postService.listPost(post);
//     }
//     /**
//      * 查询帖子详情
//      */
//     @PostMapping("/detail/{id}")
//     public void detailPost(@PathVariable Long id) {
//         postService.detailPost(id);
//     }
//     /**
//      * 收藏帖子
//      */
//     @PostMapping("/favour")
//     public void favoritePost(@RequestBody Post post) {
//         postService.favourPost(post);
//     }




// }
