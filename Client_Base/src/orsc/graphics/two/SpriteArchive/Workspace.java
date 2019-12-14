package orsc.graphics.two.SpriteArchive;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Workspace {

    private Path home;
    private String name;
    private List<Subspace> subspaces = new ArrayList<>();

    public Workspace(Path home) {
        this.home = home;
        this.name = home.getFileName().toString();
    }

    public Workspace() {}

    public String getName() { return this.name; }
    public void changeName(String name) { this.name = name; }
    public Path getHome() { return this.home; }
    public List<Subspace> getSubspaces() {
        return this.subspaces;
    }

    public Subspace getSubspaceByName(String name) {
        for (Subspace subspace : getSubspaces()) {
            if (subspace.getName().equalsIgnoreCase(name))
                return subspace;
        }

        return null;
    }

    public int getSubspaceCount() { return this.subspaces.size(); }
    public int getEntryCount() {
        int entryCount = 0;
        for (Subspace subspace : this.subspaces) {
            entryCount += subspace.getEntryCount();
        }
        return entryCount;
    }

    public int getSpriteCount() {
        int spriteCount = 0;
        for (Subspace subspace : this.subspaces) {
            spriteCount += subspace.getSpriteCount();
        }
        return spriteCount;
    }
    public int getAnimationCount() {
        int animationCount = 0;
        for (Subspace subspace : this.subspaces) {
            animationCount += subspace.getAnimationCount();
        }
        return animationCount;
    }
}

