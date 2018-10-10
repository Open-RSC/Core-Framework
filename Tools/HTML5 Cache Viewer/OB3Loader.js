(function () {


    THREE.OB3Loader = function (manager) {

        this.manager = ( manager !== undefined ) ? manager : THREE.DefaultLoadingManager;


    };

    THREE.OB3Loader.prototype = {

        constructor: THREE.OB3Loader,

        load: function (url, onLoad, onProgress, onError) {

            var scope = this;

            this._url = url;
            this._baseDir = url.substr(0, url.lastIndexOf('/') + 1);

            var loader = new THREE.XHRLoader(this.manager);
            loader.setResponseType('arraybuffer');
            loader.load(url, function (text) {

                onLoad(scope.parse(text));

            }, onProgress, onError);

        },

        parse: function (data) {
            var model = new THREE.Object3D();
            this._ptr = 0;
            this._data = new DataView(data);

            var vertexCount = this.readU16();
            var faceCount = this.readU16();

            var geometry = new THREE.BufferGeometry();
            var vertex = [];

            var vertices = [];
            var faceFillFront = [];
            var faceFillBack = [];
            var faceIntensity = [];
            var faceNumVertices = [];
            var indexes = [];
            var colors =[];
            for (var i = 0; i < vertexCount * 3; i++) {
                vertex.push(parseFloat(this.readS16()));

            }
            for (var i = 0; i < vertexCount; i++) {

                vertices.push(vertex[i],-vertex[vertexCount + i],-vertex[(vertexCount * 2) + i]);
            }

            for (var i = 0; i < faceCount; i++) {
                faceNumVertices.push(this.readU8());
            }
            var cOff = 0;
            for (var i = 0; i < faceCount; i++) {
                var c = this.readS16();

                faceFillFront.push(c);
                /*     if (faceFillFront[l1] == 32767) {
                 faceFillFront[l1] = magic;
                 }
                 */
            }

            for (var i = 0; i < faceCount; i++) {
                faceFillBack.push(this.readS16());
                /*       if (faceFillBack[i2] == 32767) {
                 faceFillBack[i2] = magic;
                 }
                 */
            }

            for (var i = 0; i < faceCount; i++) {
                var x = this.readU8();
                if (x == 0) {
                    faceIntensity.push(0);
                } else {
                    // faceIntensity[j2] = magic;
                }
            }
            for (var i = 0; i < faceCount; i++) {
                var face = new Uint16Array(faceNumVertices[i]);
                for (var x = 0; x < faceNumVertices[i]; x++) {
                    if (vertexCount < 256) {

                        face[x] = this.readU8()  & 0xff;
                    } else {

                        face[x] = this.readU16();
                    }
                }

                //convert to triangles

                for (var fc = 0; fc <= (faceNumVertices[i] / 3); fc++) {
                    indexes.push(face[fc  ], face[ fc+1 ], face[ fc+2 ]);
                    var c = this.getTextureOrColor(faceFillFront[i]);
               /*     if(c instanceof Array){

                        colors.push(c[0]);
                        colors.push(c[1]);
                        colors.push(c[2]);

                    }

                    colors.push(255);
                    colors.push(0);
                    colors.push(255);
                    */
                }

            }

            geometry.addAttribute('position', new THREE.BufferAttribute(new Float32Array(vertices), 3));

            geometry.addAttribute( 'color', new THREE.BufferAttribute(new Float32Array( colors), 3 ) );
            geometry.setIndex(new THREE.BufferAttribute(new Uint16Array(indexes), 1));
            geometry.computeFaceNormals();
            var material = new THREE.MeshBasicMaterial( new THREE.MeshBasicMaterial( { color: 0xffffff,wireframe : true} ) );
            var mesh = new THREE.Mesh(geometry,material);


            model.add(mesh);
            return model;

        },

        getTextureOrColor: function (i) {
            if (i == 32767)
                return 0xffffff; //returning white just indicate we need a transparency.
            //prepareTexture(i);
            // if (i >= 0)
            //   return texturePixels[i][0];
            if (i < 0) {
                i = -(i + 1);
                var j = i >> 10 & 0x1f;
                var k = i >> 5 & 0x1f;
                var l = i & 0x1f;

                return [j,k,l];
            } else {
                return 0xffff00; //returning aliceblue to indicate we need a texture.
            }
        },
        readU8: function () {
            var a = this._data.getInt8(this._ptr);
            this._ptr += 1;
            return a;
        },
        readU16: function () {

            var a = this._data.getUint16(this._ptr, false);
            this._ptr += 2;
            return a;

        },
        readS16: function () {
            var a = this._data.getInt16(this._ptr, false);
            this._ptr += 2;
            return a;
        }
    };

})();
