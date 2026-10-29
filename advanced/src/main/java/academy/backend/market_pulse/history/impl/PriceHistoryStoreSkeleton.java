package academy.backend.market_pulse.history.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import academy.backend.market_pulse.history.PriceHistoryStore;
import academy.backend.market_pulse.history.PricePoint;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (бинарный, текстовый и т.д.); главное,
 * чтобы каждый {@link #append} создавал НОВЫЙ файл (не переписывал предыдущий) и
 * {@link #load(Path, String)} возвращал все сохранённые точки по тикеру в порядке возрастания
 * времени. Проверьте себя тестом {@code PriceHistoryStoreContractTest} (замените в нём эталонную
 * реализацию на свою).
 */
public class PriceHistoryStoreSkeleton implements PriceHistoryStore {
    private final ObjectMapper mapper = new ObjectMapper();

    private record StoredPoint(String ticker, String timestamp, BigDecimal price) {
    }

    @Override
    public Path append(Path directory, PricePoint point) {
        try {
            StoredPoint stored = new StoredPoint(
                    point.ticker(), point.timestamp().toString(), point.price());
            String json = mapper.writeValueAsString(stored);
            Files.createDirectories(directory);
            Path file = Files.createTempFile(directory, "price-", ".json");
            Files.writeString(file, json);
            return file;
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить историю цены: " + directory, e);
        }
    }

    @Override
    public List<PricePoint> load(Path directory, String ticker) {
        if (Files.notExists(directory)) {
            return List.of();
        }
        List<PricePoint> points = new ArrayList<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory, "price-*.json")) {
            for (Path file : files) {
                String json = Files.readString(file);
                StoredPoint stored = mapper.readValue(json, StoredPoint.class);
                if (ticker.equals(stored.ticker())) {
                    Instant time = Instant.parse(stored.timestamp());
                    PricePoint point = new PricePoint(stored.ticker(), time, stored.price());
                    points.add(point);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать историю цены: " + directory, e);
        }
        points.sort(Comparator.comparing(PricePoint::timestamp));
        return points;
    }
}
