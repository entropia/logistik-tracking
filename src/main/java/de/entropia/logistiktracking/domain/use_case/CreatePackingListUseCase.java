package de.entropia.logistiktracking.domain.use_case;


import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;
import de.entropia.logistiktracking.domain.packing_list.PackingList;
import de.entropia.logistiktracking.domain.repository.EuroPalletRepository;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.utility.Result;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Transactional
@Component
public class CreatePackingListUseCase {
    private final EuroPalletRepository euroPalletRepository;
    private final PackingListRepository packingListRepository;

    public CreatePackingListUseCase(EuroPalletRepository euroPalletRepository, PackingListRepository packingListRepository) {
        this.euroPalletRepository = euroPalletRepository;
        this.packingListRepository = packingListRepository;
    }

    public Result<PackingList, CreateNewPackingListError> createNewPackingListUseCase(
        String packingListName, long placedOnPalletId
    ) {
        Optional<EuroPallet> placedOnPallet = euroPalletRepository.findEuroPallet(placedOnPalletId);
        if (placedOnPallet.isEmpty()) {
            return new Result.Error<>(CreateNewPackingListError.TargetPalletNotFound);
        }

        PackingList packingList;
        try {
            packingList = PackingList
                    .builder()
                    .name(packingListName)
                    .packedOn(placedOnPallet.get())
                    .build();
        } catch (IllegalArgumentException e) {
            return new Result.Error<>(CreateNewPackingListError.BadArguments);
        }

        packingList =packingListRepository.createNewPackingList(packingList);
        return new Result.Ok<>(packingList);
    }
}
