package com.example.tiffanytran.tk2assignment2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;

/**
 * Created by tiffanytran on 4/20/17.
 */

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap icons[];
    int[] indices = {0, 1, 2, 3, 4, 5};
    int[][] pictures = new int[9][9];
    int[][] pictures2 = new int[9][9];
    int[][] temp_pictures = new int[9][9]; //temporary pictures saves pictures array to reset if there's no valid combination
    final Random random = new Random();
    int previousX;
    int previousY;
    int NineExists = 0;
    int combinationValid = 0;
    int NoMovesRemaining = 0;
    int InsideMovesRemainingExists = 0;

    int scorevalue = 0;

    BoardView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setWillNotDraw(false);
        System.out.println("Constructor");
        icons = new Bitmap[6];
        previousX = 0;
        previousY = 0;
    }

    @Override
    public void onDraw(Canvas canvas) {
        System.out.println("onDraw()");
        canvas.drawColor(Color.WHITE); //this sets the background to white

        Rect rect = new Rect(); //creates new rectangle that fills up the whole screen
        int width = getWidth();
        int height = getHeight();
        int j;
        int i;
        int rowSize = width / 9;
        int columnSize = height / 10;


        for (i = 0; i < 9; i++) { //for loop goes through the 9x9 grid
            for (j = 1; j < 10; j++) {
                rect.set(i * rowSize, (j * columnSize), (i + 1) * rowSize, (j + 1) * columnSize); //sets individual rectangle for each candy
                canvas.drawBitmap(icons[pictures[i][j - 1]], null, rect, null); //this line draws the Bitmap
            }
        }

        Paint scoreColor = new Paint(Color.GREEN);
        scoreColor.setTextSize(40);
        String s = "Total Score:";
        scoreColor.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(s, 200, 0, scoreColor);

        Paint paintNumber = new Paint(Color.GREEN);
        paintNumber.setTextSize(40);
        String stringOfNumber;
        stringOfNumber = Integer.toString(scorevalue);
        paintNumber.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(stringOfNumber, 470, 80, scoreColor);

        if ((scorevalue > 2000) || (NoMovesRemaining == 1)) {
            canvas.drawColor(Color.WHITE); //this makes the background white
            scoreColor.setTextSize(40); //this sets the text size
            String highScore = "Your High Score is: "; //displays this at the very end of the game
            scoreColor.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(highScore, 80, 650, scoreColor);
            canvas.drawText(stringOfNumber, 480, 800, scoreColor);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("SurfaceCreated()");
        setWillNotDraw(false);

        icons[0] = BitmapFactory.decodeResource(getResources(), R.drawable.blue); //this is the blue candy
        icons[1] = BitmapFactory.decodeResource(getResources(), R.drawable.green); //this is the green candy
        icons[2] = BitmapFactory.decodeResource(getResources(), R.drawable.orange); //this is the orange candy
        icons[3] = BitmapFactory.decodeResource(getResources(), R.drawable.purple); //this is the purple candy
        icons[4] = BitmapFactory.decodeResource(getResources(), R.drawable.red); //this is the red candy
        icons[5] = BitmapFactory.decodeResource(getResources(), R.drawable.yellow); //this is the yellow candy


        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.pictures[i][j] = random.nextInt(indices.length); //this randomizes the index of the array of pictures
                pictures2[i][j] = this.pictures[i][j];
            }
        }
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                System.out.print(pictures2[i][j] + "    ");
            }
            System.out.println();
        }
        LabelingCandiesToDestroy(); //destroy initial combinations
        scorevalue = 0; //score should be 0 in the beginning of the game
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("SurfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("SurfaceDestroyed()"); //this is called after the user quits the program
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("onTouchEvent()");
        int width = getWidth();
        int height = getHeight();
        int rowSize = width / 9;
        int columnSize = height / 10;
        int currentX;
        int currentY;

        if (event.getAction() == MotionEvent.ACTION_DOWN) { //when the user presses on the screen

            previousX = (int) event.getX() / rowSize;
            previousY = (int) event.getY() / columnSize;

        } else if (event.getAction() == MotionEvent.ACTION_UP) { //when the user releases his/her touch on the screen
            currentX = (int) event.getX() / rowSize;
            currentY = (int) event.getY() / columnSize;
            //all the following if statements checks if the second x and y is one space away from it (top, bottom, left, right)
            if (previousX == currentX) {
                if ((currentY == previousY + 1) || (currentY == previousY - 1)) {
                    ValidSwitch(currentX, currentY);
                }

            } else if (previousY == currentY) {
                if ((currentX == previousX + 1) || (currentX == previousX - 1)) {
                    ValidSwitch(currentX, currentY);
                }

            } else {
                System.out.println("Not a valid switch");
            }
            invalidate(); //this refreshes the background, calls onDraw()
        }

        return true;
    }

    public void ValidSwitch(int x, int y) {//this function initiates the switch between two candies when it is valid

        SaveState();
        int temp = pictures[x][y - 1];
        pictures[x][y - 1] = pictures[previousX][previousY - 1];
        pictures[previousX][previousY - 1] = temp;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                pictures2[i][j] = pictures[i][j];
            }
        }

        combinationValid = 0;
        LabelingCandiesToDestroy();

        if (combinationValid == 0) { //if there's no combination from the switch, it resets it to the original state
            ResetState();
        } else {
            if (InsideMovesRemainingExists == 0) {//checks if there is a playable move left
                combinationValid = 0;
                MovesRemainingExists();
            }
        }
    }

    public void LabelingCandiesToDestroy() { //this function will label candies that have a 3 or more combination
        int i;
        int j;
        int checkIfSame = 1;
        int NumOfSame = 1;
        System.out.println("     ");
        //the two sets of nested for loops are used to check for combinations in different directions (top-bottom, left-right)
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                NumOfSame = 1;
                checkIfSame = 1;
                while (checkIfSame < (9 - j)) { //when there is a valid combination, it labels the candies as 9
                    if (pictures[i][j] == pictures[i][j + checkIfSame]) {
                        checkIfSame++;
                        NumOfSame++;
                    } else {
                        break;
                    }
                }
                if (NumOfSame > 2) {
                    for (int k = 0; k < NumOfSame; k++) {
                        pictures2[i][j + k] = 9; //9 is set to candies that have a 3 or more combinations
                    }
                }

            }
        }
        //this is the second set for the other direction
        for (j = 0; j < 9; j++) {
            for (i = 0; i < 9; i++) {
                NumOfSame = 1;
                checkIfSame = 1;
                while (checkIfSame < (9 - i)) {
                    if (pictures[i][j] == pictures[i + checkIfSame][j]) {
                        checkIfSame++;
                        NumOfSame++;
                    } else {
                        break;
                    }
                }
                if (NumOfSame > 2) {
                    for (int k = 0; k < NumOfSame; k++) {
                        pictures2[i + k][j] = 9;
                    }
                }

            }
        }

        for (j = 0; j < 9; j++) {
            for (i = 0; i < 9; i++) {
                System.out.print(pictures2[i][j] + "    ");
            }
            System.out.println();
        }
        DestroyCandies();
    }

    public void DestroyCandies() { //this will delete the candies that have a 3 or more combination
        //the nine that are previously labeled are deleted
        NineExists = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (pictures2[i][j] == 9) {
                    scorevalue += 10;
                    NineExists = 1;
                    combinationValid = 1;
                    NoMovesRemaining = 0;
                    for (int k = 0; k < j; k++) {
                        pictures2[i][j - k] = pictures2[i][j - k - 1]; //candies are shifted down to fill in the spaces
                    }
                    pictures2[i][0] = indices[random.nextInt(indices.length)]; //random candies are generated

                }
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                pictures[i][j] = pictures2[i][j];

            }
        }
        System.out.println("     ");
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                System.out.print(pictures2[i][j] + "    ");
            }
            System.out.println();
        }

        if ((NineExists == 1)) { //if there is a combination
            LabelingCandiesToDestroy();
        }
    }

    public void MovesRemainingExists() { //checks if there are playable moves left by shifting each candy up, down, left, and right until it finds a valid combination
        InsideMovesRemainingExists = 1;
        NoMovesRemaining = 1;
        Beginning:
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            previousX = i;
            previousY = j + 1;
            if ((i + 1) < 9) {
                ValidSwitch(i + 1, j + 1);
                if (NoMovesRemaining == 0) { //checks the candies to the right
                    ResetState();
                    break Beginning; //breaks the loop
                }
            }

            if ((i - 1) >= 0) {
                ValidSwitch(i - 1, j + 1);
                if (NoMovesRemaining == 0) { //checks the candies to the left
                    ResetState();
                    break Beginning;
                }
            }

            if ((j + 1) < 9) {
                ValidSwitch(i, j + 2);
                if (NoMovesRemaining == 0) { //checks the candies on the bottom
                    ResetState();
                    break Beginning;
                }
            }

            if ((j - 1) >= 0) {
                ValidSwitch(i, j);
                if (NoMovesRemaining == 0) { //checks the candies at the top
                    ResetState();
                    break Beginning;
                }
            }
        }

    }
        InsideMovesRemainingExists = 0;

}
    public void ResetState() { //it resets the original state if the switch is not valid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                pictures[i][j] = temp_pictures[i][j];
                pictures2[i][j] = pictures[i][j];


            }
        }
    }


    public void SaveState() { //it saves the state to be used for the reset state
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                temp_pictures[i][j] = pictures[i][j];

            }
        }
    }

}



