package ru.practicum.emojicon.model;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.practicum.emojicon.engine.*;

import java.util.*;

public class EmojiWorld extends EmojiObject implements EntityResolver, EmojiObjectHolder, Controller {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final List<EmojiWorldObject> objects = new ArrayList<>();
    private UUID selection = null;
    private List<List<Byte>> landscape;

    public EmojiWorld() {
        this.initEarth(2048, 2048);
        log.info("world created");
    }

    private void initEarth(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        List<List<Byte>> hMap = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            List<Byte> hRow = new ArrayList<>();

            for (int y = 0; y < height; y++) {
                hRow.add((byte) 0);
            }

            hMap.add(hRow);
        }
        this.landscape = hMap;
    }

    @Override
    public void drawFrame(Frame frame) {
        drawEarth(frame);
        drawObjects(frame);
    }

    private void drawObjects(Frame frame) {
        //отсекаем лишние объекты, которые точно не отобразятся
        objects.stream()
                .filter(obj -> frame.getLeft() <= obj.getLeft() && frame.getRight() >= obj.getRight() && frame.getTop() <= obj.getTop() && frame.getBottom() >= obj.getBottom())
                .forEach(obj -> {
                    TranslatedFrame objFrame = new TranslatedFrame(frame, new Point(obj.getX(), obj.getY()));
                    objFrame.setTransparentColor(TextColor.ANSI.BLACK_BRIGHT);
                    obj.drawFrame(objFrame);
                });
    }

    private void drawEarth(Frame frame) {
        for (int x = Math.max(0, frame.getLeft()); x <= Math.min(getWidth(), frame.getRight()); x++) {
            for (int y = Math.max(0, frame.getTop()); y <= Math.min(getHeight(), frame.getBottom()); y++) {
                frame.setPosition(x, y);
                frame.setFillColor(TextColor.ANSI.BLACK_BRIGHT);
                frame.paint();
            }
        }
    }

    @Override
    public UUID addObject(EmojiObject obj, Point position) {
        EmojiWorldObject wobj = new EmojiWorldObject(this, obj, position);
        addWorldObject(wobj);
        return wobj.getId();
    }

    @Override
    public boolean isFreeArea(int left, int top, int right, int bottom) {
        return left >= 0 && top >= 0 && right <= getWidth() && bottom <= getHeight(); // пока всё поле пустое
    }

    private void addWorldObject(EmojiWorldObject obj) {
        objects.add(obj);
    }

    @Override
    public void handleKey(KeyStroke key) {
        objects.stream()
                .filter(obj -> obj.getId().equals(selection))
                .filter(obj -> obj instanceof Controllable)
                .map(obj -> (Controllable) obj)
                .forEach(obj -> {
            switch (key.getKeyType()) {
                case ArrowDown:
                case ArrowLeft:
                case ArrowUp:
                case ArrowRight:
                    Point pt = null;

                    switch (key.getKeyType()) {
                        case ArrowDown:
                            pt = new Point(0, 1);
                            break;
                        case ArrowLeft:
                            pt = new Point(-1, 0);
                            break;
                        case ArrowRight:
                            pt = new Point(1, 0);
                            break;
                        case ArrowUp:
                            pt = new Point(0, -1);
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }

                    obj.move(pt);
                    break;
                default:
            }
        });
    }

    @Override
    public void setSelection(UUID... objectId) {
        this.selection = objectId[0];
    }

    @Override
    public List<UUID> getSelection() {
        return selection != null ? Arrays.asList(selection) : Collections.emptyList();
    }

    @Override
    public Optional<? extends Entity> findEntity(UUID uuid) {
        return objects.stream().filter(obj -> obj.getId().equals(uuid)).findFirst(); //TODO заменить на Map и поиск по ключу
    }
}
