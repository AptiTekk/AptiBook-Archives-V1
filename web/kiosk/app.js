/*
 * Copyright (C) 2016 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiBook, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

(function () {
    this.app = angular.module("kiosk", [
        "ngRoute"
    ]);

    this.app.controller("ResourceController", function ($scope) {
        $scope.resource = {
            name: "Meeting Room A"
        };
    });

    this.app.controller("TimeController", function ($scope, $timeout) {
        var tick = function () {
            $scope.currentTime = new Date();
            $timeout(tick, 1000 - new Date().getMilliseconds());
        };

        tick();
    });

    this.app.controller("ReservationsController", function ($scope, $timeout) {
        var tick = function () {
            //TODO: Get from server

            $scope.currentReservationIndex = -1;//-1 + Math.round(Math.random()); //Set to -1 for no current reservation.
            $scope.isAvailable = $scope.currentReservationIndex < 0;

            $scope.reservations = [
                {
                    title: "Get Seeded Planning",
                    startTime: new Date("October 31, 2016 8:00"),
                    endTime: new Date("October 31, 2016 9:00"),
                    past: true
                },
                {
                    title: "Hashtaggy Meeting",
                    startTime: new Date("October 31, 2016 12:00"),
                    endTime: new Date("October 31, 2016 13:00")
                },
                {
                    title: "AptiTekk Sales Pitch",
                    startTime: new Date("October 31, 2016 14:00"),
                    endTime: new Date("October 31, 2016 15:30")
                }
            ];
            $timeout(tick, 60000 - (new Date().getSeconds() * 1000));
        };

        tick();
    });

})();


