package com.openrsc.server.database.patches;

import com.openrsc.server.database.queries.Named;
import com.openrsc.server.database.queries.NamedParameterQuery;

public class PatchApplierQueries {
    @Named("patches.markPatchExecuted")
    public NamedParameterQuery PATCHES_MARK_PATCH_EXECUTED;

    @Named("patches.getExecutedPatches")
    public NamedParameterQuery PATCHES_GET_EXECUTED;
}
