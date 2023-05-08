package com.zk.controller;


import com.zk.dto.Result;
import com.zk.pojo.Video;
import com.zk.service.VideoService;
import com.zk.utils.AliyunOSS;
import com.zk.utils.JWTUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static com.zk.utils.RedisConstants.USER_VIDEO_KEY;

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

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/publish/action")
    public Result action(@RequestParam("token") String token,
                         @RequestParam("data") MultipartFile file,
                         @RequestParam("title") String title) {
        if (file == null || file.isEmpty()) {
            return Result.fail();
        }
        String playUrl = AliyunOSS.fileUpload(file);
        String coverUrl = AliyunOSS.videoSnapshot(playUrl);
        Video video = new Video();
        video.setTitle(title);
        video.setPlayUrl(playUrl);
        video.setCoverUrl(coverUrl);
        String userId = JWTUtils.getMemberIdByJwtToken(token);
        video.setUserId(Integer.parseInt(userId));
        boolean isSuccess = videoService.save(video);
        if (isSuccess) {
            stringRedisTemplate.opsForSet()
                    .add(USER_VIDEO_KEY + userId, video.getId().toString());
        }

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

