package academy.backend.market_pulse.repository.store.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import academy.backend.market_pulse.repository.store.WatchlistStore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, построчный текст и т.д.); главное,
 * чтобы {@code load(save(tickers))} возвращал тот же набор тикеров в том же порядке. Проверьте себя
 * тестом {@code WatchlistStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class WatchlistStoreSkeleton implements WatchlistStore {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void save(Path path, Set<String> tickers) {
        try {
            Files.createDirectories(path.toAbsolutePath().getParent());
            String json = mapper.writeValueAsString(tickers);
            Files.writeString(path, json);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить watchlist: " + path, e);
        }
    }

    @Override
    public Set<String> load(Path path) {
        try {
            String json = Files.readString(path);
            String[] tickers = mapper.readValue(json, String[].class);
            Set<String> result = new LinkedHashSet<>();
            for (String ticker : tickers) {
                result.add(ticker);
            }
            return result;
        } catch (NoSuchFileException e) {
            return new LinkedHashSet<>();
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать watchlist: " + path, e);
        }
    }
}
