/**
 * Created by Silabsoft on 8/17/2016.
 */
(function () {


    RSC.JagexTextureLoader = function (manager, config, spriteData) {

        this.manager = ( manager !== undefined ) ? manager : RSC.DefaultLoadingManager;
        this.rawSpriteCache = spriteData;
        this.surface = spriteData;
        this.config = config;
        this.textures = [];
    };

    RSC.JagexTextureLoader.prototype = {

        constructor: RSC.JagexTextureLoader,

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


            return this.parseTextures(new RSC.JagUnpack(new DataView(data)));
        },
        parseTextures: function (jagCache) {

            var index = jagCache.unpackFile("index.dat");

            var textures = this.allocateTextures(this.config.textureCount, 7, 11);

            for (var i = 0; i < this.config.textureCount; i++) {
                var name = config.textureName[i];
                var buff1 = jagCache.unpackFile(name + ".dat");
                this.surface.setBounds(0,0,128,128)
                this.surface.drawBox(0, 0, 128, 128, 0xff00ff);
                this.surface.parseSprite(this.rawSpriteCache.spriteTexture, buff1, index, 1);
                this.surface.drawSprite(0, 0, this.rawSpriteCache.spriteTexture);

                var wh = this.rawSpriteCache.spriteWidthFull[this.rawSpriteCache.spriteTexture];
                var nameSub = config.textureSubtypeName[i];
                if (nameSub != undefined && nameSub.length > 0) {
                    var buff2 = jagCache.unpackFile(nameSub + ".dat");
                    this.surface.parseSprite(this.rawSpriteCache.spriteTexture, buff2, index, 1);
                    this.surface.drawSprite(0, 0, this.rawSpriteCache.spriteTexture);
                }
                this.surface.fillSpritePixelsFromDrawingArea( this.rawSpriteCache.spriteTextureWorld + i, 0, 0, wh, wh);
                this.textures.push(  this.surface.getCanvas());

            }
            return this.textures;
        },

        allocateTextures: function (count, something7, something11) {
            var textures;
            return textures = {
                textureCount: count,
                textureColoursUsed: this.createArray(count, 0),
                textureColourList: this.createArray(count, 0),
                textureDimension: [count],
                textureLoadedNumber: [count],
                textureBackTransparent: [count],
                texturePixels: this.createArray(count, 0),
                textureCountLoaded: 0,
                textureColours64: this.createArray(count, 0),// 64x64 rgba
                textureColours128: this.createArray(count, 0),// 128x128 rgba
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
