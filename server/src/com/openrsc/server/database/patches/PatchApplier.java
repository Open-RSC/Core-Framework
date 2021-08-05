package com.openrsc.server.database.patches;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class PatchApplier {
    private static final Logger LOGGER = LogManager.getLogger();

    public boolean applyPatches() {
        List<File> patches = getPatches(getExecutedPatches());
        Collection<String> executedPatches = new ArrayList<>(patches.size());
        if (patches.isEmpty()) {
            LOGGER.info("Database patches are up to date");
            return true;
        }
        for (File patch : patches) {
            LOGGER.info("Applying database patch " + patch.getName());
            if (!applyPatch(patch)) {
                return false;
            }
            executedPatches.add(patch.getName());
        }
        executedPatches.forEach(this::markPatchExecuted);
        return true;
    }

    protected abstract void markPatchExecuted(String fileName);

    protected abstract Collection<String> getExecutedPatches();

    protected abstract boolean applyPatch(File file);

    protected abstract URI getPatchDirectory();

    protected List<File> getPatches(Collection<String> alreadyExecutedPatches) {
        File directory = new File(getPatchDirectory());
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            // Filter the patches to include only those after lastRunDate, or all if lastRunDate is null
            // Sort sequentially so that we apply patches in the proper order
            return Arrays.stream(files)
                    // Exclude directories, files with less than 10 characters (YYYY_MM_DD), and files not containing .sql
                    .filter(file -> file.isFile() && file.getName().length() > 10 && file.getName().contains(".sql"))
                    .filter(file -> !alreadyExecutedPatches.contains(file.getName()))
                    .map(this::createLocalDateFilePair)
                    .sorted(Map.Entry.comparingByKey())
                    .filter(Objects::nonNull)
                    .map(Pair::getValue)
                    .collect(Collectors.toList());
        } else {
            throw new PatchApplicationException(
                    MessageFormat.format(
                            "Unable to apply database patches: {0} does not exist or is not a directory",
                            directory.getAbsolutePath()
                    )
            );
        }
    }

    private Pair<LocalDate, File> createLocalDateFilePair(File file) {
        String fileName = file.getName();
        String formattedDate = fileName.substring(0, 10).replaceAll("_", "-");
        try {
            LocalDate associatedDate = LocalDate.parse(formattedDate);

            return Pair.of(associatedDate, file);
        } catch (Exception ex) {
            LOGGER.warn(
                    MessageFormat.format(
                            "Skipping patch {0}: does not follow format YYYY_MM_DD_patch_description.sql",
                            file.getName()
                    ),
                    ex
            );
        }
        return null;
    }
}
