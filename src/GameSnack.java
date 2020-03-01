/*
 Classic Game Snake
 06.02.2020
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;



public class GameSnack {

    final String TITLE_OF_PROGRAM = "Classic Game Snake";
    final String GAME_OVER_MSG = "GAME OVER";

    final int POINT_RADIUS = 20;        // в пикселях
    final int FIELD_HEIGHT = 20;        // в поинтах
    final int FIELD_WIDTH = 30;
    final int FIELD_DX = 6;
    final int FIELD_DY = 28;
    final int START_LOCATION = 200;

    final int START_SNAKE_SIZE = 6;
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    final int SHOW_DELAY = 150;

    final int LEFT = 37;
    final int RIGHT = 39;
    final int UP = 38;
    final int DOWN = 40;
    final int START_DIRECTION = RIGHT;

    final Color DEFAULT_COLOR = Color.BLACK;
    final Color FOOD_COLOR = Color.GREEN;
    final Color POISON_COLOR = Color.RED;

    Snake snake;
    Food food;
    //Poison poison;

    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();
    boolean gameOver = false;


    public static void main(String[] args) {
        new GameSnack().go();
    }

    void go(){
        frame = new JFrame(TITLE_OF_PROGRAM + " : " + START_SNAKE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //просто закрытие окна
        //размеры окна
        frame.setSize(FIELD_WIDTH * POINT_RADIUS + FIELD_DX, FIELD_HEIGHT * POINT_RADIUS + FIELD_DY);
        frame.setLocation(START_LOCATION,START_LOCATION); //стартовое положение
        frame.setResizable(false); //нельзя менять размер окна

        canvasPanel = new Canvas(); //тут все рисуется
        //canvasPanel.setBackground(Color.WHITE);

        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel); // добавляем нашу канву на нашу панель, тобишь белое поле в окне)
//так как канва одна, то занимает все поле целиком
        frame.addKeyListener(new KeyAdapter() { //обработчик нажатия клавиш

            @Override
            public void keyPressed(KeyEvent e) {        //именно нажатия клавиш
               snake.setDirection(e.getKeyCode());
               //System.out.println(e.getKeyCode());
            }
        });

        frame.setVisible(true);

        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, START_DIRECTION);
        food = new Food();

        while (!gameOver){  //запускаем змейку и отрисовываем, плюс задержка

            snake.move();
            if (food.isEaten()){        //смотрим съедена ли еда, если да, то создаем новую
                food.next();
            }
            canvasPanel.repaint();
            try{
                Thread.sleep(SHOW_DELAY);
            }catch (InterruptedException e){e.printStackTrace();}
        }
    }

    class Snake { //ну наша змея
        ArrayList<Point> snake = new ArrayList<>();
        int direction;

        public Snake(int x, int y, int length, int direction){ //конструктор
            for (int i=0; i< length; i++){
                Point point = new Point(x-i, y);
                snake.add(point);
            }
            this.direction = direction;
        }

        boolean isInsideSnake(int x, int y){
            for (Point point : snake) {
                if ((point.getX() == x) && (point.getY() == y)) {return true;}
            }
            return false;
        }

        void paint(Graphics g){
            for (Point point : snake) {
                point.paint(g);
            }
        }
//надо чтобы совпали координаты головы змеи и еды
        boolean isFood(Point food){
            return ((snake.get(0).getX() == food.getX()) && (snake.get(0).getY() == food.getY()));
        }

        void move(){    //движение змеи

            int x = snake.get(0).getX();
            int y = snake.get(0).getY();

            if (direction == LEFT) x--;
            if (direction == RIGHT) x++;
            if (direction == UP) y--;
            if (direction == DOWN) y++;

/*если за край выползает, то выходит с другой стороны*/
            if (x > FIELD_WIDTH - 1) x = 0;
            if (x < 0) x = FIELD_WIDTH - 1;
            if (y > FIELD_HEIGHT - 1) y = 0;
            if (y < 0) y = FIELD_HEIGHT - 1;

            gameOver = isInsideSnake(x, y); //проверка пересекаем ли мы сами себя

            snake.add(0, new Point(x,y)); //добавляем змейке новый элемент
            //смотрит столкнулась ли голова змеи с едой
            if (isFood(food)){
                food.eat();
                frame.setTitle(TITLE_OF_PROGRAM + " : " + snake.size());
            } else {
                snake.remove(snake.size() - 1); //удаляем элемент с хвоста
            }
        }

        void setDirection(int direction){
            if (direction >= LEFT && direction <= DOWN){
                if (Math.abs(this.direction - direction) != 2){
                this.direction = direction;}
            }
        }
    }

    class Food extends Point{

        public Food() {
            super(-1, -1);
            this.color = FOOD_COLOR;
        }

        void eat() {
            this.setXY(-1,-1);
        }

        boolean isEaten(){
           return this.getX() == -1;
        }

        void next(){
            int x, y;
            do {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);
        }
    }

/*    public class Poison {
        ArrayList<Point> poison = new ArrayList<>();
        Color color = POISON_COLOR;

        boolean isPoison(int x, int y){
            for (GameSnack.Point point: poison) {
                if ((point.getX() == x) && (point.getY() == y)) return true;
            }
            return false;
        }

        void add(){
            int x, y;
            do {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (isPoison(x, y) || snake.isInsideSnake(x,y) || food.isFood(x, y));
            poison.add(new Point(x, y, color));
        }

        void paint(Graphics g){
            for (Point point : poison) {
                point.paint(g); }
        }
    }*/


    class Point {  //базовый объект точка
        int x,y;
        Color color = DEFAULT_COLOR;

        public Point(int x, int y){
            this.setXY(x, y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        void setXY(int x, int y){   //установка координат метод
            this.x = x;
            this.y = y;
        }

        void paint(Graphics g){ //рисует
            g.setColor(color);
            g.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }
    }

    public class Canvas extends JPanel {     //канва

        @Override
        public void paint(Graphics g){
            super.paint(g);
            snake.paint(g); //рисуем змею
            food.paint(g);

            if (gameOver){
                g.setColor(Color.RED);
                g.setFont(new Font("Atial", Font.BOLD, 40));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(GAME_OVER_MSG, (FIELD_WIDTH * POINT_RADIUS + FIELD_DX - fm.stringWidth
                        (GAME_OVER_MSG))/2, (FIELD_HEIGHT * POINT_RADIUS + FIELD_DY) / 2);
            }
        }
    }
}
