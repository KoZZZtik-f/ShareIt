package ru.yandex.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.yandex.practicum.shareit.util.DatabaseCleanupService;

@SpringBootApplication
public class ShareitApplication {

	private final DatabaseCleanupService databaseCleanupService;

	public ShareitApplication(DatabaseCleanupService databaseCleanupService) {
		this.databaseCleanupService = databaseCleanupService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ShareitApplication.class, args);
	}

	/*
	это событие, которое Spring генерирует, когда контекст приложения полностью
	инициализирован и готов к использованию. Это происходит после того, как все
	бины созданы, зависимости внедрены, и контекст "освежен" (refresh).
	 */
	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshed() {
		databaseCleanupService.cleanupDatabase(); // Очистка базы данных после инициализации контекста
	}
}