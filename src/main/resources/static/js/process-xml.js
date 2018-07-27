var app = angular.module('process',['checklist-model']);

app.controller('ProcessXML', function($scope, $http) {
    $scope.data = {
        model: null
    };

    $http.get("http://localhost:8099/listbusinessunits").then(function (response) {
        $scope.data.bu = response.data;
    });

    $scope.submit = function() {
        $http({
            method: 'POST',
            url: "http://localhost:8099/processxml/",

        }).then(function successCallback(response) {
            console.log("SUCCESS");
        }, function errorCallback(response) {
            console.log("ERROR" + response.toString());
        });
    }
}