var fs = require("fs");
//get a reference to the uglify-js module
var UglifyJS = require('uglify-js');



function readFiles(dirname, onFileContent, onError) {
    fs.readdir(dirname, function (err, filenames) {
        if (err) {
            onError(err);
            return;
        }
        filenames.forEach(function (filename) {
            let path = dirname + "/" + filename;
            if (fs.lstatSync(path).isFile() && filename.endsWith(".js")) {
                fs.readFile(path, 'utf-8', function (err, content) {
                    if (err) {
                        onError(err);
                        return;
                    }
                    onFileContent(path, content);
                });
            } else if (fs.lstatSync(path).isDirectory()) {
                console.log("recursive call with dir = " + path);
                readFiles(path, onFileContent, onError)
            }

        });
    });
}

function obfucicateFile(file,text) {
    fs.unlink(file,function(err){
        if(err) return console.log(err);
        console.log(file+' deleted successfully');
        var result = UglifyJS.minify(text);
        // console.log(result.code)
        fs.writeFile(file,result.code,'utf8',err1 => console.log(err1));
        console.log("done !! "+file)
    });
}

readFiles('web', function (filename, content) {
    console.log("JS File  detected " + filename);
    obfucicateFile(filename, content)

}, function (err) {
    throw err;
});

// console.log(obfuscationResult.getObfuscatedCode());