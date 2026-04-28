package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.strategy.RingShootStrategy;

import java.util.List;

/**
 * HeroAircraft 的轻量级单元测试入口。
 *
 * 由于当前工程未引入 JUnit，这里使用自定义断言和 main 方法执行测试。
 */
public class HeroAircraftTest {

    private int passedCount;
    private int failedCount;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        new HeroAircraftTest().runAll();
    }

    private void runAll() {
        runTest("shoot 使用默认直射策略", this::testShootUsesDefaultStraightStrategy);
        runTest("applyBuff 和 clearBuff 版本控制正确", this::testBuffVersionProtectsLatestState);
        runTest("decreaseHp 归零后会失效", this::testDecreaseHpMakesHeroInvalidAtZero);
        runTest("increaseHp 不会超过最大生命值", this::testIncreaseHpCapsAtMaxHp);

        if (failedCount > 0) {
            throw new AssertionError("HeroAircraftTest 失败: " + failedCount + " 项，成功: " + passedCount + " 项");
        }

        System.out.println("HeroAircraftTest 全部通过，共 " + passedCount + " 项。");
    }

    private void runTest(String testName, Runnable testCase) {
        try {
            testCase.run();
            passedCount++;
            System.out.println("[PASS] " + testName);
        } catch (AssertionError error) {
            failedCount++;
            System.err.println("[FAIL] " + testName + " -> " + error.getMessage());
        }
    }

    private HeroAircraft resetHero() {
        HeroAircraft heroAircraft = HeroAircraft.getInstance();
        heroAircraft.resetForNewGame();
        heroAircraft.setLocation(200, 500);
        return heroAircraft;
    }

    private void testShootUsesDefaultStraightStrategy() {
        HeroAircraft heroAircraft = resetHero();

        List<BaseBullet> bullets = heroAircraft.shoot();

        assertEquals(3, bullets.size(), "默认应发射 3 颗子弹");
        assertTrue(bullets.get(0) instanceof HeroBullet, "英雄机应创建 HeroBullet");
        assertEquals(180, bullets.get(0).getLocationX(), "第一颗子弹的横坐标应向左偏移");
        assertEquals(200, bullets.get(1).getLocationX(), "第二颗子弹应位于正前方");
        assertEquals(220, bullets.get(2).getLocationX(), "第三颗子弹的横坐标应向右偏移");
        assertEquals(494, bullets.get(0).getLocationY(), "子弹纵坐标应出现在英雄机前方");
        assertEquals(-30, bullets.get(0).getSpeedY(), "默认直射子弹应向上飞行");
    }

    private void testBuffVersionProtectsLatestState() {
        HeroAircraft heroAircraft = resetHero();
        heroAircraft.setShootNum(7);
        heroAircraft.setShootStrategy(new RingShootStrategy());

        int lowPriorityVersion = heroAircraft.applyBuff(1);
        int highPriorityVersion = heroAircraft.applyBuff(2);

        assertTrue(lowPriorityVersion > 0, "第一次 Buff 申请应成功");
        assertTrue(highPriorityVersion > lowPriorityVersion, "高优先级 Buff 应颁发更新版本号");

        heroAircraft.clearBuff(lowPriorityVersion);
        assertEquals(7, heroAircraft.getShootNum(), "旧版本线程不应清除最新 Buff 状态");
        assertEquals(7, heroAircraft.shoot().size(), "旧版本清除失败后应继续保留当前弹幕配置");

        heroAircraft.clearBuff(highPriorityVersion);
        assertEquals(heroAircraft.getOriginalShootNum(), heroAircraft.getShootNum(), "当前版本线程应恢复默认子弹数");
        assertEquals(3, heroAircraft.shoot().size(), "恢复后应重新使用默认直射配置");
    }

    private void testDecreaseHpMakesHeroInvalidAtZero() {
        HeroAircraft heroAircraft = resetHero();

        heroAircraft.decreaseHp(heroAircraft.getHp());

        assertEquals(0, heroAircraft.getHp(), "生命值归零后应被钳制为 0");
        assertTrue(heroAircraft.notValid(), "生命值归零后英雄机应失效");
    }

    private void testIncreaseHpCapsAtMaxHp() {
        HeroAircraft heroAircraft = resetHero();
        int maxHp = heroAircraft.getHp();

        heroAircraft.decreaseHp(500);
        heroAircraft.increaseHp(800);

        assertEquals(maxHp, heroAircraft.getHp(), "加血后不应超过初始最大生命值");
        assertTrue(!heroAircraft.notValid(), "正常加血不会让英雄机失效");
    }

    private void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + "，expected=" + expected + ", actual=" + actual);
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}