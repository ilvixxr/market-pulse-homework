package academy.backend.market_pulse.repository.store.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import academy.backend.market_pulse.client.InstrumentQuoteMapper;
import academy.backend.market_pulse.dto.InstrumentQuoteDto;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.repository.store.QuoteFileStore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, CSV, построчный текст); главное,
 * чтобы {@code load(save(quotes))} возвращал те же котировки. Проверьте себя тестом
 * {@code QuoteFileStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class QuoteFileStoreSkeleton implements QuoteFileStore {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void save(Path path, List<Quote> quotes) {
        List<InstrumentQuoteDto> dtos = new ArrayList<>();
        for (Quote quote : quotes) {
            dtos.add(InstrumentQuoteMapper.toDto(quote));
        }
        try {
            Files.createDirectories(path.toAbsolutePath().getParent());
            String json = mapper.writeValueAsString(dtos);
            Files.writeString(path, json);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить котировки: " + path, e);
        }
    }

    @Override
    public List<Quote> load(Path path) {
        try {
            String json = Files.readString(path);
            InstrumentQuoteDto[] dtos = mapper.readValue(json, InstrumentQuoteDto[].class);

            List<Quote> quotes = new ArrayList<>();
            for (InstrumentQuoteDto dto : dtos) {
                quotes.add(InstrumentQuoteMapper.toQuote(dto));
            }
            return quotes;
        } catch (NoSuchFileException e) {
            return List.of();
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать котировки: " + path, e);
        }
    }
}
