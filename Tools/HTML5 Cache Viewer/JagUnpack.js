(function () {


    RSC.JagUnpack = function (data) {
        this._ptr = 0;
        this._data = data;
        this._data = this.parseHeader(data);

    };

    RSC.JagUnpack.prototype = {

        constructor: RSC.JagUnpack,

        parseHeader: function (data) {
            var archiveSize = 0;
            var archiveSizeCompressed = 0;

            var archiveSize = ((this.read8() & 0xff) << 16) + ((this.read8() & 0xff) << 8) + (this.read8() & 0xff);
            var archiveSizeCompressed = ((this.read8() & 0xff) << 16) + ((this.read8() & 0xff) << 8) + (this.read8() & 0xff);

            if (archiveSizeCompressed != archiveSize) {
                data = BZLib.decompress(Array.prototype.slice.call(new Int8Array(this._data.buffer.slice(6))), archiveSizeCompressed, archiveSize);
                this._ptr = 0;
                return  new DataView(data);
            }
            else {
               return  new DataView(this._data.buffer.slice(6));
            }
        },
        unpackFile: function (filename) {
            var numEntries = (this.peek8(0) & 0xff) * 256 + (this.peek8(1) & 0xff);

            var wantedHash = 0;
            filename = filename.toUpperCase();

            for (var l = 0; l < filename.length; l++) {
                //had to use Math.imul to simulate signed 32 int overflow
                wantedHash = ( Math.imul(wantedHash, 61) + filename.charCodeAt(l)) - 32;
            }

            var offset = 2 + numEntries * 10;
            for (var entry = 0; entry < numEntries; entry++) {

                var fileHash = Math.imul((this.peek8(entry * 10 + 2) & 0xff), 0x1000000) + Math.imul((this.peek8(entry * 10 + 3) & 0xff), 0x10000) + Math.imul((this.peek8(entry * 10 + 4) & 0xff), 256) + (this.peek8(entry * 10 + 5) & 0xff);
                var fileSize = Math.imul((this.peek8(entry * 10 + 6) & 0xff), 0x10000) + Math.imul((this.peek8(entry * 10 + 7) & 0xff), 256) + (this.peek8(entry * 10 + 8) & 0xff);
                var fileSizeCompressed = Math.imul((this.peek8(entry * 10 + 9) & 0xff), 0x10000) + Math.imul((this.peek8(entry * 10 + 10) & 0xff), 256) + (this.peek8(entry * 10 + 11) & 0xff);

                if (fileHash == wantedHash) {

                    if (fileSize != fileSizeCompressed) {


                        var out = [];
                        BZLib.decompress(out, fileSize, Array.prototype.slice.call(new Int8Array(this._data.buffer)), fileSizeCompressed, offset);
                        return new DataView(out);


                    } else {

                        return new DataView(this._data.buffer.slice(offset, offset + fileSize));

                    }

                }
                offset += fileSizeCompressed;
            }
            console.log("returning null");
            return null;
        },
        peek8: function (offset) {
            var a = this._data.getInt8(offset);
            return a;
        },

        read8: function () {
            var a = this._data.getInt8(this._ptr);
            this._ptr += 1;
            return a;
        },
    }

})();
