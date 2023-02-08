package ru.practicum.emojicon.engine;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.screen.Screen;
import com.vdurmont.emoji.Emoji;

//корневой фрейм работает с устройством, всегда рисует от 0 до края консоли
public class RootFrame extends AbstractFrame {
    Screen screen;

    public RootFrame(Screen screen, int right, int bottom) {
        super(0, 0, right, bottom);
        this.screen = screen;
    }

    public static RootFrame extend(Frame frame) {
        return (RootFrame) frame.getRoot();
    }

    //set position for painting
    public void setPosition(int x, int y) {
        if (x < 0 || x > getRight() || y < 0 || y > getBottom()) {
            throw new IllegalArgumentException("position out of bounds");
        }

        setPosX(x);
        setPosY(y);
    }


    //paint it with background color
    public Point paint() {
        screen.setCharacter(getPosX(), getPosY(), TextCharacter.DEFAULT_CHARACTER.withCharacter(' ').withBackgroundColor(getRealFillColor()));
        return new Point(1, 1);
    }

    //draw single character or
    public Point draw(Character character) {
        screen.setCharacter(getPosX(), getPosY(), TextCharacter.DEFAULT_CHARACTER.withCharacter(character).withForegroundColor(getColor()).withBackgroundColor(getRealFillColor()));
        return new Point(1, 1);
    }

    //draw emoji
    //some emoji take more than one symbol
    public Point draw(Emoji emoji) {
        TextCharacter[] chars = TextCharacter.fromString(emoji.getUnicode());

        for (TextCharacter aChar : chars) {
            screen.setCharacter(getPosX(), getPosY(), aChar.withForegroundColor(getColor()).withBackgroundColor(getRealFillColor()));
        }

        return new Point(chars.length, 1);
    }

    @Override
    public Frame getRoot() {
        return this;
    }

    public Screen getScreen() {
        return screen;
    }
}
