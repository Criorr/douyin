package com.zk.controller;


import com.zk.dto.Result;
import com.zk.pojo.Video;
import com.zk.service.VideoService;
import com.zk.utils.AliyunOSS;
import com.zk.utils.JWTUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zk
 * @since 2023-04-23
 */
@RestController
@RequestMapping("/douyin")
public class VideoController {
    @Resource
    VideoService videoService;

    @PostMapping("/publish/action")
    public Result action(@RequestParam("token") String token,
                         @RequestParam("data") MultipartFile file,
                         @RequestParam("title") String title) {
        if (file == null || file.isEmpty()) {
            return Result.fail();
        }
        AliyunOSS aliyunOSS = new AliyunOSS();
        String url = aliyunOSS.fileUpload(file);
        Video video = new Video();
        video.setTitle(title);
        video.setPlayUrl(url);
        String userId = JWTUtils.getMemberIdByJwtToken(token);
        video.setUserId(Integer.parseInt(userId));
        videoService.save(video);
        // TODO 存入redis
        return Result.ok();
    }

    @GetMapping("/feed")
    public Result feed(@RequestParam(value = "latest_time", required = false) String latestTime,
                       @RequestParam(value = "token", required = false) String token) {

        String userId = JWTUtils.getMemberIdByJwtToken(token);
        return videoService.feed(userId, latestTime);
    }


    @GetMapping("/publish/list")
    public Result list(@RequestParam(value = "user_id") String userId,
                       @RequestParam(value = "token") String token) {
        Integer curUserId = Integer.parseInt(JWTUtils.getMemberIdByJwtToken(token));
        return videoService.listByUserId(Integer.parseInt(userId), curUserId);
    }

}

