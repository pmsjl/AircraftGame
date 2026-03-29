package edu.hitsz.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * 专门用于播放 .wav 音频的后台线程
 */
public class MusicThread extends Thread {
    // 音频文件路径
    private final String filename;
    // 是否循环播放（BGM需要循环，音效不需要）
    private final boolean isLoop;

    public MusicThread(String filename, boolean isLoop) {
        this.filename = filename;
        this.isLoop = isLoop;
    }

    @Override
    public void run() {
        // 利用循环结构，配合 isLoop 控制是否重复播放
        do {
            try {
                // 1. 获取音频输入流
                File audioFile = new File(filename);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioStream.getFormat();

                // 2. 配置数据行 (DataLine)
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine dataLine = (SourceDataLine) AudioSystem.getLine(info);

                // 3. 打开并开始播放
                dataLine.open(format);
                dataLine.start();

                // 4. 【多线程核心】创建一个缓冲区，一边读一边播
                int count;
                byte[] buffer = new byte[1024];
                while ((count = audioStream.read(buffer, 0, buffer.length)) != -1) {
                    dataLine.write(buffer, 0, count);
                }

                // 5. 播放完毕，清理资源
                dataLine.drain();
                dataLine.close();
                audioStream.close();

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }

        } while (isLoop); // 如果是 BGM (isLoop 为 true)，播完一遍后继续 do 循环
    }
}