public interface CanShoot {
    public default void shoot() {
        return;
    }

    public default void shoot(GameScreenLevelTwo gameScreen) {
        return;
    }
}
