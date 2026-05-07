// Mock extension - stub implementation for main target compilation
// Real DeviceActivityMonitor logic lives in the separate extension target

struct DeviceActivityMonitorExtensionStub {
    static func handleIntervalStart() {}
    static func handleIntervalEnd() {}
}
