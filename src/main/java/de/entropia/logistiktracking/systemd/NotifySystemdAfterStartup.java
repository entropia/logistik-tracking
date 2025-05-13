package de.entropia.logistiktracking.systemd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

@Slf4j
@Configuration
@Profile("notify-systemd")
public class NotifySystemdAfterStartup {
	@EventListener(ApplicationReadyEvent.class)
	public void whenStartedUpNotifySystemd() {
		// bissl cursed
		try (Arena arena = Arena.ofConfined()) {
			Linker linker = Linker.nativeLinker();
			// hard coded library path for libsystemd.so. default lookup cant find it
			// this doesnt work for anything not specifically amd64. too bad!
			SymbolLookup systemd = SymbolLookup.libraryLookup(Path.of("/usr/lib/x86_64-linux-gnu/libsystemd.so.0"), arena);
			MemorySegment notifyFunction = systemd.findOrThrow("sd_notify");
			// int sd_notify(int, char*) -> int sd_notify(int, ptr_t)
			FunctionDescriptor functionDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
			MethodHandle handle = linker.downcallHandle(notifyFunction, functionDescriptor);

			// send READY=1 to systemd to let it know we're booted up
			MemorySegment memorySegment = arena.allocateFrom("READY=1");
			// DO NOT remove this cast. dynamic methods rely on an explicit cast to figure out their signature
			// without it, the method would assume void return type, and fail at runtime because that's wrong.
			int _ = (int)handle.invokeExact(0, memorySegment);
		} catch (Throwable t) {
			log.error("Failed to notify systemd:", t);
		}
	}
}
