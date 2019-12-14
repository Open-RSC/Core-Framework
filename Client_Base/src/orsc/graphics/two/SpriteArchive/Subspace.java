package orsc.graphics.two.SpriteArchive;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Subspace {
    private Path home;
    private String name = "";
    private List<Entry> entryList = new ArrayList<>();

    @Override
    public String toString() { return getName(); }

    public Subspace(String name) {
        this.name = name;
    }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
    public Path getHome() { return this.home; }

    public List<Entry> getEntryList() { return entryList; }

    public int getEntryCount() { return this.entryList.size(); }

    public int getSpriteCount() {
        int spriteCount = 0;
        for (Entry entry : entryList) {
            if (entry.getFrames().length == 1)
                ++spriteCount;
        }
        return spriteCount;
    }
    public int getAnimationCount() {
        int animationCount = 0;
        for (Entry entry : entryList) {
            if (entry.getFrames().length > 1)
                ++animationCount;
        }
        return animationCount;
    }
}
