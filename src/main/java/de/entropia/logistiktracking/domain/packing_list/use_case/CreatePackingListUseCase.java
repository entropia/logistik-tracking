package de.entropia.logistiktracking.domain.packing_list.use_case;


import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.openapi.model.NewPackingListDto;
import de.entropia.logistiktracking.openapi.model.PackingListDto;
import de.entropia.logistiktracking.utility.Result;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Transactional
@Component
public class CreatePackingListUseCase {
    private final EuroPalletRepository euroPalletRepository;
    private final PackingListRepository packingListRepository;
    private final PackingListConverter packingListConverter;

    public CreatePackingListUseCase(EuroPalletRepository euroPalletRepository, PackingListRepository packingListRepository, PackingListConverter packingListConverter) {
        this.euroPalletRepository = euroPalletRepository;
        this.packingListRepository = packingListRepository;
        this.packingListConverter = packingListConverter;
    }

    public Result<PackingListDto, CreateNewPackingListError> createNewPackingListUseCase(NewPackingListDto newPackingListDto) {
        long placedOnPalletId;
        try {
            placedOnPalletId = Long.parseLong(newPackingListDto.getPackedOnPallet());
        } catch (NumberFormatException e) {
            return new Result.Error<>(CreateNewPackingListError.BadArguments);
        }

        Optional<EuroPallet> placedOnPallet = euroPalletRepository.findEuroPallet(placedOnPalletId);
        if (placedOnPallet.isEmpty()) {
            return new Result.Error<>(CreateNewPackingListError.TargetPalletNotFound);
        }

        PackingList packingList;
        try {
            packingList = PackingList
                    .builder()
                    .name(newPackingListDto.getName())
                    .packedOn(placedOnPallet.get())
                    .build();
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(CreateNewPackingListError.BadArguments);
        }

        packingList = packingListRepository.createNewPackingList(packingList);
        return new Result.Ok<>(packingListConverter.toDto(packingList));
    }
}
