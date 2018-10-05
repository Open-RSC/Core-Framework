/**
 * Created by Silabsoft on 8/17/2016.
 */
(function () {


    RSC.MediaLoader = function (manager, config, spriteData) {
        this.manager = ( manager !== undefined ) ? manager : THREE.DefaultLoadingManager;
        this.surface = spriteData;
        this.config = config;
    };

    RSC.MediaLoader.prototype = {

        constructor: RSC.MediaLoader,

        load: function (url, onLoad, onProgress, onError) {
            var scope = this;
            this._url = url;
            this._baseDir = url.substr(0, url.lastIndexOf('/') + 1);
            var loader = new THREE.XHRLoader(this.manager);
            loader.setResponseType('arraybuffer');
            loader.load(url, function (text) {
                onLoad(scope.parseData(text));

            }, onProgress, onError);

        },

        parseData: function (data) {

            return this.parseMedia(new RSC.JagUnpack(new DataView(data)));
        },
        parseMedia: function (jagCache) {
            var buff = jagCache.unpackFile("index.dat");
            surface.parseSprite(surface.spriteMedia,jagCache.unpackFile("inv1.dat"), buff, 1);
            surface.parseSprite(surface.spriteMedia + 1, jagCache.unpackFile("inv2.dat"), buff, 6);
            surface.parseSprite(surface.spriteMedia + 9, jagCache.unpackFile("bubble.dat"), buff, 1);
            surface.parseSprite(surface.spriteMedia + 10, jagCache.unpackFile("runescape.dat"), buff, 1);
            surface.parseSprite(surface.spriteMedia + 11, jagCache.unpackFile("splat.dat"), buff, 3);
            surface.parseSprite(surface.spriteMedia + 14,jagCache.unpackFile("icon.dat"), buff, 8);
            surface.parseSprite(surface.spriteMedia + 22,jagCache.unpackFile("hbar.dat"), buff, 1);
            surface.parseSprite(surface.spriteMedia + 23, jagCache.unpackFile("hbar2.dat"), buff, 1);
            surface.parseSprite(surface.spriteMedia + 24, jagCache.unpackFile("compass.dat"), buff, 1);
            surface.parseSprite(surface.spriteMedia + 25, jagCache.unpackFile("buttons.dat"), buff, 2);
            surface.parseSprite(surface.spriteUtil,jagCache.unpackFile("scrollbar.dat"), buff, 2);
            surface.parseSprite(surface.spriteUtil + 2, jagCache.unpackFile("corners.dat"), buff, 4);
            surface.parseSprite(surface.spriteUtil + 6, jagCache.unpackFile("arrows.dat"), buff, 2);
            surface.parseSprite(surface.spriteProjectile, jagCache.unpackFile("projectile.dat"), buff, this.config.projectileSprite);

            var i = config.itemSpriteCount;
            for (var j = 1; i > 0; j++) {
                var k = i;
                i -= 30;
                if (k > 30) {
                    k = 30;
                }
                surface.parseSprite(surface.spriteItem + (j - 1) * 30,  jagCache.unpackFile("objects" + j + ".dat"), buff, k);
            }

            surface.loadSprite(surface.spriteMedia);
            surface.loadSprite(surface.spriteMedia + 9);
            for (var l = 11; l <= 26; l++) {
                surface.loadSprite(surface.spriteMedia + l);
            }

            for (var i1 = 0; i1 < config.projectileSprite; i1++) {
                surface.loadSprite(surface.spriteProjectile + i1);
            }

            for (var j1 = 0; j1 < config.itemSpriteCount; j1++) {
                surface.loadSprite(surface.spriteItem + j1);
            }

            return surface;
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
