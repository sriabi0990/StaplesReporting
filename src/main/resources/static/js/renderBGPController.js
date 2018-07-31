var app = angular.module('process',['checklist-model']);

app.controller('renderBGP', function($scope, $http, $sce) {
    $scope.data = {
        tablecontents : null
    };
    $scope.downloadfileshow = false;

    $scope.getHtml = function(html){
        return $sce.trustAsHtml(html);
    };

    $http.get("http://localhost:8099/getBGPReport").then(function (response) {
        var r = response.data.toString();
        $scope.data.tablecontents = r;

    });

});

app.filter('html', function($sce) {
    return function(val) {
        return $sce.trustAsHtml(val);
    };
});