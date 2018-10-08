"Generated from Java with JSweet 1.1.0 - http://www.jsweet.org";
var BZLib = (function () {
    function BZLib() {
    }
    BZLib.decompress = function (out, outSize, __in, inSize, offset) {
        if (((out != null && out instanceof Array) || out === null) && ((typeof outSize === 'number') || outSize === null) && ((__in != null && __in instanceof Array) || __in === null) && ((typeof inSize === 'number') || inSize === null) && ((typeof offset === 'number') || offset === null)) {
            return (function () {
                var block = new BZState();
                block.input = __in;
                block.nextIn = offset;
                block.output = out;
                block.availOut = 0;
                block.availIn = inSize;
                block.decompressedSize = outSize;
                block.bsLive = 0;
                block.bsBuff = 0;
                block.totalInLo32 = 0;
                block.totalInHi32 = 0;
                block.totalOutLo32 = 0;
                block.totalOutHi32 = 0;
                block.blockNo = 0;
                BZLib.decompress(block);
                outSize -= block.decompressedSize;
                return outSize;
            })();
        }
        else if (((out != null && out instanceof BZState) || out === null) && outSize === undefined && __in === undefined && inSize === undefined && offset === undefined) {
            return BZLib.decompress$BZState(out);
        }
        else
            throw new Error('invalid overload '+ __in instanceof Array ? "true" : "false");
    };
    BZLib.nextHeader = function (state) {
        var cStateOutCh = state.stateOutCh;
        var cStateOutLen = state.stateOutLen;
        var cNblockUsed = state.nblockUsed;
        var cK0 = state.k0;
        var cTt = state.tt;
        var cTpos = state.tpos;
        var output = state.output;
        var csNextOut = state.availOut;
        var csAvailOut = state.decompressedSize;
        var asdasdasd = csAvailOut;
        var sSaveNblockPP = state.saveNblock + 1;
        returnNotr: do {
            if (cStateOutLen > 0) {
                do {
                    if (csAvailOut === 0)
                        break returnNotr;
                    if (cStateOutLen === 1)
                        break;
                    output[csNextOut] = cStateOutCh;
                    cStateOutLen--;
                    csNextOut++;
                    csAvailOut--;
                } while ((true));
                if (csAvailOut === 0) {
                    cStateOutLen = 1;
                    break;
                }
                output[csNextOut] = cStateOutCh;
                csNextOut++;
                csAvailOut--;
            }
            var flag = true;
            while ((flag)) {
                flag = false;
                if (cNblockUsed === sSaveNblockPP) {
                    cStateOutLen = 0;
                    break returnNotr;
                }
                cStateOutCh = (cK0 | 0);
                cTpos = cTt[cTpos];
                var k1 = ((cTpos & 255) | 0);
                cTpos >>= 8;
                cNblockUsed++;
                if (k1 !== cK0) {
                    cK0 = k1;
                    if (csAvailOut === 0) {
                        cStateOutLen = 1;
                    }
                    else {
                        output[csNextOut] = cStateOutCh;
                        csNextOut++;
                        csAvailOut--;
                        flag = true;
                        continue;
                    }
                    break returnNotr;
                }
                if (cNblockUsed !== sSaveNblockPP)
                    continue;
                if (csAvailOut === 0) {
                    cStateOutLen = 1;
                    break returnNotr;
                }
                output[csNextOut] = cStateOutCh;
                csNextOut++;
                csAvailOut--;
                flag = true;
            }
            ;
            cStateOutLen = 2;
            cTpos = cTt[cTpos];
            var k2 = ((cTpos & 255) | 0);
            cTpos >>= 8;
            if (++cNblockUsed !== sSaveNblockPP)
                if (k2 !== cK0) {
                    cK0 = k2;
                }
                else {
                    cStateOutLen = 3;
                    cTpos = cTt[cTpos];
                    var k3 = ((cTpos & 255) | 0);
                    cTpos >>= 8;
                    if (++cNblockUsed !== sSaveNblockPP)
                        if (k3 !== cK0) {
                            cK0 = k3;
                        }
                        else {
                            cTpos = cTt[cTpos];
                            var byte3 = ((cTpos & 255) | 0);
                            cTpos >>= 8;
                            cNblockUsed++;
                            cStateOutLen = (byte3 & 255) + 4;
                            cTpos = cTt[cTpos];
                            cK0 = ((cTpos & 255) | 0);
                            cTpos >>= 8;
                            cNblockUsed++;
                        }
                }
        } while ((true));
        var i2 = state.totalOutLo32;
        state.totalOutLo32 += asdasdasd - csAvailOut;
        if (state.totalOutLo32 < i2)
            state.totalOutHi32++;
        state.stateOutCh = cStateOutCh;
        state.stateOutLen = cStateOutLen;
        state.nblockUsed = cNblockUsed;
        state.k0 = cK0;
        state.tt = cTt;
        state.tpos = cTpos;
        state.output = output;
        state.availOut = csNextOut;
        state.decompressedSize = csAvailOut;
    };
    BZLib.decompress$BZState = function (state) {
        var gMinLen = 0;
        var gLimit = null;
        var gBase = null;
        var gPerm = null;
        state.blocksize100k = 1;
        if (state.tt == null)
            state.tt = new Array(state.blocksize100k * 100000);
        var goingandshit = true;
        while ((goingandshit)) {
            var uc = BZLib.getUchar(state);
            if (uc === 23)
                return;
            uc = BZLib.getUchar(state);
            uc = BZLib.getUchar(state);
            uc = BZLib.getUchar(state);
            uc = BZLib.getUchar(state);
            uc = BZLib.getUchar(state);
            state.blockNo++;
            uc = BZLib.getUchar(state);
            uc = BZLib.getUchar(state);
            uc = BZLib.getUchar(state);
            uc = BZLib.getUchar(state);
            uc = BZLib.getBit(state);
            state.blockRandomised = uc !== 0;
            if (state.blockRandomised)
                console.info("PANIC! RANDOMISED BLOCK!");
            state.origPtr = 0;
            uc = BZLib.getUchar(state);
            state.origPtr = state.origPtr << 8 | uc & 255;
            uc = BZLib.getUchar(state);
            state.origPtr = state.origPtr << 8 | uc & 255;
            uc = BZLib.getUchar(state);
            state.origPtr = state.origPtr << 8 | uc & 255;
            for (var i = 0; i < 16; i++) {
                uc = BZLib.getBit(state);
                state.inUse_16[i] = uc === 1;
            }
            for (var i = 0; i < 256; i++)
                state.inUse[i] = false;
            for (var i = 0; i < 16; i++)
                if (state.inUse_16[i]) {
                    for (var j = 0; j < 16; j++) {
                        uc = BZLib.getBit(state);
                        if (uc === 1)
                            state.inUse[i * 16 + j] = true;
                    }
                }
            BZLib.makeMaps(state);
            var alphaSize = state.nInUse + 2;
            var nGroups = BZLib.getBits(3, state);
            var nSelectors = BZLib.getBits(15, state);
            for (var i = 0; i < nSelectors; i++) {
                var j = 0;
                do {
                    uc = BZLib.getBit(state);
                    if (uc === 0)
                        break;
                    j++;
                } while ((true));
                state.selectorMtf[i] = (j | 0);
            }
            var pos = new Array(6);
            for (var v = 0; v < nGroups; v++)
                pos[v] = v;
            for (var i = 0; i < nSelectors; i++) {
                var v = state.selectorMtf[i];
                var tmp = pos[v];
                for (; v > 0; v--)
                    pos[v] = pos[v - 1];
                pos[0] = tmp;
                state.selector[i] = tmp;
            }
            for (var t = 0; t < nGroups; t++) {
                var curr = BZLib.getBits(5, state);
                for (var i = 0; i < alphaSize; i++) {
                    do {
                        uc = BZLib.getBit(state);
                        if (uc === 0)
                            break;
                        uc = BZLib.getBit(state);
                        if (uc === 0)
                            curr++;
                        else
                            curr--;
                    } while ((true));
                    state.len[t][i] = (curr | 0);
                }
            }
            for (var t = 0; t < nGroups; t++) {
                var minLen = 32;
                var maxLen = 0;
                for (var l1 = 0; l1 < alphaSize; l1++) {
                    if (state.len[t][l1] > maxLen)
                        maxLen = state.len[t][l1];
                    if (state.len[t][l1] < minLen)
                        minLen = state.len[t][l1];
                }
                BZLib.createDecodeTables(state.limit[t], state.base[t], state.perm[t], state.len[t], minLen, maxLen, alphaSize);
                state.minLens[t] = minLen;
            }
            var eob = state.nInUse + 1;
            var nblockMax = 100000 * state.blocksize100k;
            var groupNo = -1;
            var groupPos = 0;
            for (var i = 0; i <= 255; i++)
                state.unzftab[i] = 0;
            var kk = 4095;
            for (var ii = 15; ii >= 0; ii--) {
                for (var jj = 15; jj >= 0; jj--) {
                    state.mtfa[kk] = ((ii * 16 + jj) | 0);
                    kk--;
                }
                state.mtfbase[ii] = kk + 1;
            }
            var nblock = 0;
            if (groupPos === 0) {
                groupNo++;
                groupPos = 50;
                var gSel = state.selector[groupNo];
                gMinLen = state.minLens[gSel];
                gLimit = state.limit[gSel];
                gPerm = state.perm[gSel];
                gBase = state.base[gSel];
            }
            groupPos--;
            var zn = gMinLen;
            var zvec;
            var zj;
            for (zvec = BZLib.getBits(zn, state); zvec > gLimit[zn]; zvec = zvec << 1 | zj) {
                zn++;
                zj = BZLib.getBit(state);
            }
            for (var nextSym = gPerm[zvec - gBase[zn]]; nextSym !== eob;)
                if (nextSym === 0 || nextSym === 1) {
                    var es = -1;
                    var N = 1;
                    do {
                        if (nextSym === 0)
                            es += N;
                        else if (nextSym === 1)
                            es += 2 * N;
                        N *= 2;
                        if (groupPos === 0) {
                            groupNo++;
                            groupPos = 50;
                            var gSel = state.selector[groupNo];
                            gMinLen = state.minLens[gSel];
                            gLimit = state.limit[gSel];
                            gPerm = state.perm[gSel];
                            gBase = state.base[gSel];
                        }
                        groupPos--;
                        var zn_2 = gMinLen;
                        var zvec_2;
                        var zj_2;
                        for (zvec_2 = BZLib.getBits(zn_2, state); zvec_2 > gLimit[zn_2]; zvec_2 = zvec_2 << 1 | zj_2) {
                            zn_2++;
                            zj_2 = BZLib.getBit(state);
                        }
                        nextSym = gPerm[zvec_2 - gBase[zn_2]];
                    } while ((nextSym === 0 || nextSym === 1));
                    es++;
                    uc = state.setToUnseq[state.mtfa[state.mtfbase[0]] & 255];
                    state.unzftab[uc & 255] += es;
                    for (; es > 0; es--) {
                        state.tt[nblock] = uc & 255;
                        nblock++;
                    }
                }
                else {
                    var nn = nextSym - 1;
                    if (nn < 16) {
                        var pp = state.mtfbase[0];
                        uc = state.mtfa[pp + nn];
                        for (; nn > 3; nn -= 4) {
                            var z = pp + nn;
                            state.mtfa[z] = state.mtfa[z - 1];
                            state.mtfa[z - 1] = state.mtfa[z - 2];
                            state.mtfa[z - 2] = state.mtfa[z - 3];
                            state.mtfa[z - 3] = state.mtfa[z - 4];
                        }
                        for (; nn > 0; nn--)
                            state.mtfa[pp + nn] = state.mtfa[(pp + nn) - 1];
                        state.mtfa[pp] = uc;
                    }
                    else {
                        var lno = (nn / 16 | 0);
                        var off = nn % 16;
                        var pp = state.mtfbase[lno] + off;
                        uc = state.mtfa[pp];
                        for (; pp > state.mtfbase[lno]; pp--)
                            state.mtfa[pp] = state.mtfa[pp - 1];
                        state.mtfbase[lno]++;
                        for (; lno > 0; lno--) {
                            state.mtfbase[lno]--;
                            state.mtfa[state.mtfbase[lno]] = state.mtfa[(state.mtfbase[lno - 1] + 16) - 1];
                        }
                        state.mtfbase[0]--;
                        state.mtfa[state.mtfbase[0]] = uc;
                        if (state.mtfbase[0] === 0) {
                            kk = 4095;
                            for (var ii = 15; ii >= 0; ii--) {
                                for (var jj = 15; jj >= 0; jj--) {
                                    state.mtfa[kk] = state.mtfa[state.mtfbase[ii] + jj];
                                    kk--;
                                }
                                state.mtfbase[ii] = kk + 1;
                            }
                        }
                    }
                    state.unzftab[state.setToUnseq[uc & 255] & 255]++;
                    state.tt[nblock] = state.setToUnseq[uc & 255] & 255;
                    nblock++;
                    if (groupPos === 0) {
                        groupNo++;
                        groupPos = 50;
                        var gSel = state.selector[groupNo];
                        gMinLen = state.minLens[gSel];
                        gLimit = state.limit[gSel];
                        gPerm = state.perm[gSel];
                        gBase = state.base[gSel];
                    }
                    groupPos--;
                    var zn_2 = gMinLen;
                    var zvec_2;
                    var zj_2;
                    for (zvec_2 = BZLib.getBits(zn_2, state); zvec_2 > gLimit[zn_2]; zvec_2 = zvec_2 << 1 | zj_2) {
                        zn_2++;
                        zj_2 = BZLib.getBit(state);
                    }
                    nextSym = gPerm[zvec_2 - gBase[zn_2]];
                }
            state.stateOutLen = 0;
            state.stateOutCh = 0;
            state.cftab[0] = 0;
            for (var i = 1; i <= 256; i++)
                state.cftab[i] = state.unzftab[i - 1];
            for (var i = 1; i <= 256; i++)
                state.cftab[i] += state.cftab[i - 1];
            for (var i = 0; i < nblock; i++) {
                uc = ((state.tt[i] & 255) | 0);
                state.tt[state.cftab[uc & 255]] |= i << 8;
                state.cftab[uc & 255]++;
            }
            state.tpos = state.tt[state.origPtr] >> 8;
            state.nblockUsed = 0;
            state.tpos = state.tt[state.tpos];
            state.k0 = ((state.tpos & 255) | 0);
            state.tpos >>= 8;
            state.nblockUsed++;
            state.saveNblock = nblock;
            BZLib.nextHeader(state);
            goingandshit = state.nblockUsed === state.saveNblock + 1 && state.stateOutLen === 0;
        }
        ;
    };
    BZLib.getUchar = function (state) {
        return (BZLib.getBits(8, state) | 0);
    };
    BZLib.getBit = function (state) {
        return (BZLib.getBits(1, state) | 0);
    };
    BZLib.getBits = function (i, state) {
        var vvv;
        do {
            if (state.bsLive >= i) {
                var v = state.bsBuff >> state.bsLive - i & (1 << i) - 1;
                state.bsLive -= i;
                vvv = v;
                break;
            }
            state.bsBuff = state.bsBuff << 8 | state.input[state.nextIn] & 255;
            state.bsLive += 8;
            state.nextIn++;
            state.availIn--;
            state.totalInLo32++;
            if (state.totalInLo32 === 0)
                state.totalInHi32++;
        } while ((true));
        return vvv;
    };
    BZLib.makeMaps = function (state) {
        state.nInUse = 0;
        for (var i = 0; i < 256; i++)
            if (state.inUse[i]) {
                state.setToUnseq[state.nInUse] = (i | 0);
                state.nInUse++;
            }
    };
    BZLib.createDecodeTables = function (limit, base, perm, length, minLen, maxLen, alphaSize) {
        var pp = 0;
        for (var i = minLen; i <= maxLen; i++) {
            for (var j = 0; j < alphaSize; j++)
                if (length[j] === i) {
                    perm[pp] = j;
                    pp++;
                }
        }
        for (var i = 0; i < 23; i++)
            base[i] = 0;
        for (var i = 0; i < alphaSize; i++)
            base[length[i] + 1]++;
        for (var i = 1; i < 23; i++)
            base[i] += base[i - 1];
        for (var i = 0; i < 23; i++)
            limit[i] = 0;
        var vec = 0;
        for (var i = minLen; i <= maxLen; i++) {
            vec += base[i + 1] - base[i];
            limit[i] = vec - 1;
            vec <<= 1;
        }
        for (var i = minLen + 1; i <= maxLen; i++)
            base[i] = (limit[i - 1] + 1 << 1) - base[i];
    };
    return BZLib;
}());
var BZState = (function () {
    function BZState() {
        this.nextIn = 0;
        this.availIn = 0;
        this.totalInLo32 = 0;
        this.totalInHi32 = 0;
        this.availOut = 0;
        this.decompressedSize = 0;
        this.totalOutLo32 = 0;
        this.totalOutHi32 = 0;
        this.stateOutCh = 0;
        this.stateOutLen = 0;
        this.blockRandomised = false;
        this.bsBuff = 0;
        this.bsLive = 0;
        this.blocksize100k = 0;
        this.blockNo = 0;
        this.origPtr = 0;
        this.tpos = 0;
        this.k0 = 0;
        this.nblockUsed = 0;
        this.nInUse = 0;
        this.saveNblock = 0;
        this.unzftab = new Array(256);
        this.cftab = new Array(257);
        this.inUse = new Array(256);
        this.inUse_16 = new Array(16);
        this.setToUnseq = new Array(256);
        this.mtfa = new Array(4096);
        this.mtfbase = new Array(16);
        this.selector = new Array(18002);
        this.selectorMtf = new Array(18002);
        this.len = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
            return undefined;
        }
        else {
            var array = [];
            for (var i = 0; i < dims[0]; i++) {
                array.push(allocate(dims.slice(1)));
            }
            return array;
        } }; return allocate(dims); })([6, 258]);
        this.limit = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
            return undefined;
        }
        else {
            var array = [];
            for (var i = 0; i < dims[0]; i++) {
                array.push(allocate(dims.slice(1)));
            }
            return array;
        } }; return allocate(dims); })([6, 258]);
        this.base = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
            return undefined;
        }
        else {
            var array = [];
            for (var i = 0; i < dims[0]; i++) {
                array.push(allocate(dims.slice(1)));
            }
            return array;
        } }; return allocate(dims); })([6, 258]);
        this.perm = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
            return undefined;
        }
        else {
            var array = [];
            for (var i = 0; i < dims[0]; i++) {
                array.push(allocate(dims.slice(1)));
            }
            return array;
        } }; return allocate(dims); })([6, 258]);
        this.minLens = new Array(6);
    }
    return BZState;
}());