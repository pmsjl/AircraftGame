package edu.hitsz.enemy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.StraightShootStrategy;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

/**
 * 护盾机
 * 拥有独立的护盾血量，子弹先打盾，盾破后才掉真血。
 * 护盾激活时，机体外覆盖一圈半透明蓝色光圈。
 */
public class ShieldEnemy extends AbstractAircraft {

    private static final int SHIELD_HP_MAX = 50;
    private int shieldHp;

    /** 缓存两套图像，避免每帧重绘 */
    private final BufferedImage baseImage;
    private final BufferedImage shieldedImage;

    public ShieldEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = 35;
        this.shootNum = 1;
        this.power = 15;
        this.direction = 1;
        this.shootStrategy = new StraightShootStrategy();
        this.originalSpeedX = speedX;
        this.originalSpeedY = speedY;
        this.shieldHp = SHIELD_HP_MAX;

        // 预生成带蓝色护盾光圈的合成图
        this.baseImage = ImageManager.get(this);
        this.shieldedImage = createShieldedImage(baseImage);
    }

    /**
     * 在原图周围绘制半透明蓝色光圈，作为护盾视觉效果
     */
    private static BufferedImage createShieldedImage(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int pad = 8;
        BufferedImage out = new BufferedImage(w + pad * 2, h + pad * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 先画原图（居中）
        g.drawImage(src, pad, pad, null);
        // 再画半透明蓝色椭圆边框
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
        g.setColor(new Color(80, 180, 255));
        g.setStroke(new BasicStroke(3f));
        g.drawOval(2, 2, out.getWidth() - 4, out.getHeight() - 4);
        // 内圈微亮
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
        g.setColor(new Color(180, 220, 255));
        g.drawOval(5, 5, out.getWidth() - 10, out.getHeight() - 10);
        g.dispose();
        return out;
    }

    @Override
    public BufferedImage getImage() {
        return shieldHp > 0 ? shieldedImage : baseImage;
    }

    @Override
    public int getWidth() {
        return getImage().getWidth();
    }

    @Override
    public int getHeight() {
        return getImage().getHeight();
    }

    /**
     * 子弹先扣盾，溢出部分才扣真血
     */
    @Override
    public void decreaseHp(int decrease) {
        if (shieldHp > 0) {
            int absorbed = Math.min(shieldHp, decrease);
            shieldHp -= absorbed;
            decrease -= absorbed;
            if (shieldHp == 0) {
                System.out.println("护盾机的护盾被打破！");
            }
        }
        if (decrease > 0) {
            super.decreaseHp(decrease);
        }
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public BaseBullet createBullet(int x, int y, int speedX, int speedY, int power) {
        return new EnemyBullet(x, y, speedX, speedY, power);
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.Shoot(this, 5);
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> res = new LinkedList<>();
        double num = Math.random();
        AbstractProp prop = null;
        String propName = "";
        if (num < 0.15) {
            prop = PropFactory.createProp("Blood", this.getLocationX(), this.getLocationY());
            propName = "加血道具";
        } else if (num < 0.25) {
            prop = PropFactory.createProp("Fire", this.getLocationX(), this.getLocationY());
            propName = "火力道具";
        } else if (num < 0.35) {
            prop = PropFactory.createProp("SuperFire", this.getLocationX(), this.getLocationY());
            propName = "超级火力道具";
        } else if (num < 0.40) {
            prop = PropFactory.createProp("Freeze", this.getLocationX(), this.getLocationY());
            propName = "冰冻道具";
        }
        if (prop != null) {
            res.add(prop);
            System.out.println("护盾机坠毁，掉落了：" + propName + "！");
        }
        return res;
    }

    @Override
    public void updateOnUnfreeze() {
        this.speedX = this.originalSpeedX;
        this.speedY = this.originalSpeedY;
    }

    @Override
    public int updateOnBomb() {
        // 炸弹无视护盾直接消灭
        this.vanish();
        return this.score;
    }

    @Override
<<<<<<< HEAD
    public int updateOnFreeze() {
        this.speedX = 0;
        this.speedY = 0;
        // 护盾机归普通机类，永久静止直至离场或死亡
        return -1;
=======
    public boolean updateOnFreeze() {
        this.speedX = 0;
        this.speedY = 0;
        return true;
>>>>>>> 1aaca805a7a8a470b7b73812a01b7a44b78a1d49
    }
}
