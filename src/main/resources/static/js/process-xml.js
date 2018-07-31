var app = angular.module('process',['checklist-model']);

app.controller('ProcessXML', function($scope, $http) {
    $scope.showfiles = false;
    $scope.data = {
        model: null
    };

    $http.get("http://localhost:8099/listbusinessunits").then(function (response) {
        $scope.data.bu = response.data;
    });

    $scope.displayFiles = function() {
        $http({
            method: 'GET',
            url: "http://localhost:8099/listXMLFiles/"+$scope.data.model,
        }).then(function successCallback(response) {
            console.log("SUCCESS");
            $scope.showfiles = true;
            $scope.data.files = response.data;
            // if(document.getElementById('listfiles') != null) {
            //     document.getElementById('listfiles').remove();
            // }
            // var newEle = angular.element("<div id='files'><a href='http://localhost:8099/download/SEAUK'>Download File</a> </div>");
            // var target = document.getElementById('listfiles');
            // angular.element(target).append(newEle);
        }, function errorCallback(response) {
            console.log("ERROR" + response.toString());
        });
    }

    $scope.submit = function() {
        $http({
            method: 'POST',
            url: "http://localhost:8099/processxml/"+$scope.data.model,

        }).then(function successCallback(response) {
            console.log("SUCCESS");
        }, function errorCallback(response) {
            console.log("ERROR" + response.toString());
        });
    }

    $scope.reset = function() {
        $scope.data.model = null;
        $scope.showfiles = false;
    }
});