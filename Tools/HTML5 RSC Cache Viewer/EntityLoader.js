/**
 * Created by Silabsoft on 8/17/2016.
 */
(function () {


    RSC.EntityLoader = function (manager, config, spriteData) {

        this.manager = ( manager !== undefined ) ? manager : THREE.DefaultLoadingManager;
        this.rawSpriteCache = spriteData;
        this.surface = spriteData;
        this.config = config;
        this.textures = [];
    };

    RSC.EntityLoader.prototype = {

        constructor: RSC.EntityLoader,

        load: function (url,urlB, onLoad, onProgress, onError) {
            var scope = this;
            var loader = new THREE.XHRLoader(this.manager);
            loader.setResponseType('arraybuffer');
            loader.load(url, function (text) {
                scope.dataA = text;
                var loaderb = new THREE.XHRLoader(this.manager);
                loaderb.setResponseType('arraybuffer');
                loaderb.load(urlB, function (text) {
                    onLoad(scope.parseData(scope.dataA,text));

                }, onProgress, onError);

            }, onProgress, onError);
        },

        parseData: function (data) {


            return this.parseEntities(new RSC.JagUnpack(new DataView(this.dataA)),new RSC.JagUnpack(new DataView(data)));
        },
        parseEntities: function (entityCache,entityMemberCache) {


            var indexDat = entityCache.unpackFile("index.dat");
            console.log(indexDat);
            console.log(entityMemberCache);
            var frameCount = 0;
            var anInt659 = 0;
            var anInt660 = anInt659;

           var indexDatMem =  entityMemberCache.unpackFile("index.dat");
            for (var j = 0; j < this.config.animationCount; j++) {
                var animName = this.config.animationName[j];
                for (var k = 0; k < j; k++) {
                    if (!this.config.animationName[k].localeCompare(animName)) {
                        continue;
                    }
                    this.config.animationNumber[j] = this.config.animationNumber[k];
                    break;
                }
                var dat = entityCache.unpackFile(animName + ".dat");
                var indexData = indexDat;
                if (dat == null ) {
                    dat = entityMemberCache.unpackFile(animName + ".dat");
                    indexData = indexDatMem;
                }
                if (dat != null) {

                    surface.parseSprite(anInt660, dat, indexData, 15);
                    frameCount += 15;
                    if (this.config.animationHasA[j] == 1) {
                        var datA = entityCache.unpackFile(animName + "a.dat");
                        var aIndexDat = indexDat;
                        if (datA == null ) {
                            datA =  entityMemberCache.unpackFile(animName + "a.dat");
                            aIndexDat = indexDatMem;
                        }
                        surface.parseSprite(anInt660 + 15, datA, aIndexDat, 3);

                        frameCount += 3;
                    }
                    if (this.config.animationHasF[j] == 1) {
                        var datF = entityCache.unpackFile(animName + "f.dat");
                        var fDatIndex = indexDat;
                        if (datF == null) {
                            datF = entityMemberCache.unpackFile(animName + "f.dat");
                            fDatIndex = indexDatMem;
                        }
                        surface.parseSprite(anInt660 + 18, datF, fDatIndex, 9);
                        frameCount += 9;
                    }
                    if (this.config.animationSomething[j] != 0) {
                        for (var l = anInt660; l < anInt660 + 27; l++) {
                            surface.loadSprite(l);
                        }

                    }
                }
                this.config.animationNumber[j] = anInt660;
                anInt660 += 27;
            }

        },

        createArray: function (length) {
            var arr = new Array(length || 0),
                i = length;

            if (arguments.length > 1) {
                var args = Array.prototype.slice.call(arguments, 1);
                while (i--) arr[length - 1 - i] = this.createArray.apply(this, args);
            }

            return arr;
        },

    };

})();
