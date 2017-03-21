/*global cordova, module*/
module.exports = {	
    getPartsList: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "PartsArena", "getPartsList", []);
    },
    search: function (successCallback, errorCallback, searchBrand, searchTerm) {
        cordova.exec(successCallback, errorCallback, "PartsArena", "search", [searchBrand, searchTerm]);
    }
};