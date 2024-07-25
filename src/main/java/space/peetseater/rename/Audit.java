package space.peetseater.rename;

public record Audit<T>(T value, String msg) {}
