/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceshooter.gfx;


/**
 *
 * @author Harikesh
 */
public class GameSprites {
    public static final Sprite[] LASERS = new Sprite[]{
        new Sprite("/sprites/bullets/laserGreen07.png"),
        new Sprite("/sprites/bullets/laserBlue15.png"),
        new Sprite("/sprites/bullets/laserRed15.png"),
    };
    public static final Sprite[] MISSILES = new Sprite[]{
        new Sprite("/sprites/bullets/spaceMissiles_011.png"),
        new Sprite("/sprites/bullets/spaceMissiles_010.png"),
        new Sprite("/sprites/bullets/spaceMissiles_009.png"),
    };
    public static final Sprite[] G_MISSILES = new Sprite[]{
        new Sprite("/sprites/bullets/spaceMissiles_023.png"),
        new Sprite("/sprites/bullets/spaceMissiles_022.png"),
        new Sprite("/sprites/bullets/spaceMissiles_021.png"),
    };
    public static final Sprite[][] PLAYER = new Sprite[][]{
        {
            new Sprite("/sprites/player/playerShip1_red.png"),
            new Sprite("/sprites/player/playerShip1_orange.png"),
            new Sprite("/sprites/player/playerShip1_green.png"),
            new Sprite("/sprites/player/playerShip1_blue.png"),
        },
        {
            new Sprite("/sprites/player/playerShip2_red.png"),
            new Sprite("/sprites/player/playerShip2_orange.png"),
            new Sprite("/sprites/player/playerShip2_green.png"),
            new Sprite("/sprites/player/playerShip2_blue.png"),
        },
        {
            new Sprite("/sprites/player/playerShip3_red.png"),
            new Sprite("/sprites/player/playerShip3_orange.png"),
            new Sprite("/sprites/player/playerShip3_green.png"),
            new Sprite("/sprites/player/playerShip3_blue.png"),
        },
    };
    public static final Sprite[] ENEMY = new Sprite[]{
        new Sprite("/sprites/enemies/enemyBlack1.png"),
        new Sprite("/sprites/enemies/enemyBlack3.png"),
        new Sprite("/sprites/enemies/enemyBlack4.png"),
        new Sprite("/sprites/enemies/enemyBlack2.png"),
    };
    public static final Sprite[] BOSS = new Sprite[]{
        new Sprite("/sprites/enemies/bosses/spaceShips_002.png"),
        new Sprite("/sprites/enemies/bosses/spaceShips_005.png")
    };
    public static final Sprite[] SMOKE_ANIMATION_SPRITES = new Sprite[]{
        // smoke
        new Sprite("/sprites/effects/smoke_0.png"),
        new Sprite("/sprites/effects/smoke_1.png"),
        new Sprite("/sprites/effects/smoke_2.png"),
        new Sprite("/sprites/effects/smoke_3.png"),
        new Sprite("/sprites/effects/smoke_4.png"),
        new Sprite("/sprites/effects/smoke_5.png"),
        new Sprite("/sprites/effects/smoke_6.png"),
        new Sprite("/sprites/effects/smoke_7.png"),
        new Sprite("/sprites/effects/smoke_8.png"),
    };
    
    public static final Sprite[] SHIELD_ANIMATION_SPRITES = new Sprite[]{
        // shield
        new Sprite("/sprites/effects/shield_0.png"),
        new Sprite("/sprites/effects/shield_1.png"),
        new Sprite("/sprites/effects/shield_2.png"),
    };
    
    //public static final Sprite MENU_BG = new Sprite("/sprites/menuBG.png");
    public static final Sprite[] MENU_BG = new Sprite[]{
        new Sprite("/sprites/building/spaceBuilding_009.png"),
        new Sprite("/sprites/building/spaceBuilding.png"),
    };
    
    public static final Sprite[] UI_SPRITES = new Sprite[]{
        new Sprite("/sprites/ui/weapons_panel.png"),
        new Sprite("/sprites/ui/health_panel.png"),
        new Sprite("/sprites/ui/square_disabled.png"),
        new Sprite("/sprites/ui/square_red.png"),
        new Sprite("/sprites/ui/square_blue.png"),
        new Sprite("/sprites/ui/compass_panel.png"),
        new Sprite("/sprites/ui/compass_pointer.png"),
        new Sprite("/sprites/ui/stats_panel.png"),
        new Sprite("/sprites/ui/square_yellow.png"),
        new Sprite("/sprites/ui/square_add.png"),
    };
}
