package co.edu.eci.blueprints.dto.websocket;

import co.edu.eci.blueprints.model.Point;

import java.time.LocalDateTime;
import java.util.List;

public record BlueprintWebSocketMessage(
        String type,  // "POINT_ADDED", "BLUEPRINT_CREATED", "BLUEPRINT_UPDATED", "BLUEPRINT_DELETED"
        String author,
        String blueprintName,
        Point addedPoint,
        List<Point> allPoints,
        int totalPoints,
        String message,
        LocalDateTime timestamp
) {
    public static BlueprintWebSocketMessage pointAdded(String author, String blueprintName,
                                                      Point addedPoint, List<Point> allPoints) {
        return new BlueprintWebSocketMessage(
                "POINT_ADDED",
                author,
                blueprintName,
                addedPoint,
                allPoints,
                allPoints != null ? allPoints.size() : 0,
                "Point added to blueprint",
                LocalDateTime.now()
        );
    }

    public static BlueprintWebSocketMessage blueprintCreated(String author, String blueprintName,
                                                           List<Point> points) {
        return new BlueprintWebSocketMessage(
                "BLUEPRINT_CREATED",
                author,
                blueprintName,
                null,
                points,
                points != null ? points.size() : 0,
                "Blueprint created",
                LocalDateTime.now()
        );
    }
}