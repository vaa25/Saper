/**
 * @author Alexander Vlasov
 */
public enum Level {

    Professional{
        private final int  width=30;
        private final int height=16;
        private final int bombs=99;
        public int getWidth() {

            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getBombs() {
            return bombs;
        }
    },
    Medium{
        private final int width=16;
        private final int height=16;
        private final int bombs=40;
        public int getWidth() {

            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getBombs() {
            return bombs;
        }
    },
    Novice{
        private final int width=10;
        private final int height=10;
        private final int bombs=10;
        public int getWidth() {

            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getBombs() {
            return bombs;
        }
    },
    Custom{
        private int width;
        private int height;
        private int bombs;
        public int getWidth() {

            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getBombs() {
            return bombs;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setBombs(int bombs) {
            this.bombs = bombs;
        }
    };
}
