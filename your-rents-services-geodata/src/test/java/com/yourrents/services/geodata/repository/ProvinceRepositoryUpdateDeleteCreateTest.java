package com.yourrents.services.geodata.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataConflictException;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Province;
import com.yourrents.services.geodata.model.ProvinceLocalData;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestYourRentsGeoDataServiceApplication.class)
@Transactional
class ProvinceRepositoryUpdateDeleteCreateTest {


	@Autowired
	ProvinceRepository provinceRepository;

	@Test
	void createNewProvinceInVeneto() {

		UUID venetoUuid = findVenetoUuid();
		Province newProvince = new Province(null, "New Province",
				new ProvinceLocalData("10", "NP"),
				new Province.Region(venetoUuid, null));

		Province result = provinceRepository.create(newProvince);

		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("New Province");
		assertThat(result.region().name()).isEqualTo("Veneto");
		assertThat(result.region().uuid()).isEqualTo(venetoUuid);
		ProvinceLocalData provinceLocalData = result.localData();
		assertThat(provinceLocalData).isNotNull();
		assertThat(provinceLocalData.itCodiceIstat()).isEqualTo("10");
		assertThat(provinceLocalData.itSigla()).isEqualTo("NP");
	}

	@Test
	void createNewProvinceWithInvalidRegion() {
		UUID randomUUID = UUID.randomUUID();
		Province newProvince = new Province(null, "New Province",
				null,
				new Province.Region(randomUUID, null));
		assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
						provinceRepository.create(newProvince))
				.withMessageContaining(randomUUID.toString())
				.withNoCause();
	}

	@Test
	void createNewProvinceWithNoRegionAndNoLocalData() {
		Province newProvince = new Province(null, "New Province",
				null,
				null);
		Province result = provinceRepository.create(newProvince);
		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("New Province");
		assertThat(result.region()).isNull();
		assertThat(result.localData()).isNull();
	}

	@Test
	void deleteAnExistingProvince() {
		Searchable filterForVenezia = FilterCriteria.of(
				FilterCondition.of("name", "eq", "Venezia"));
		Page<Province> provincePage = provinceRepository.find(filterForVenezia,
				PageRequest.ofSize(1));
		Province province = provincePage.getContent().get(0);
		UUID veneziaUuid = province.uuid();
		assertThatExceptionOfType(DataConflictException.class).isThrownBy(() ->
						provinceRepository.delete(veneziaUuid))
				.withMessageContaining(province.uuid().toString());
	}

	@Test
	void deleteANotExistingProvince() {
		UUID randomUUID = UUID.randomUUID();
		assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
						provinceRepository.delete(randomUUID))
				.withMessageContaining(randomUUID.toString());
	}

	@Test
	void updateAnExistingProvince() {
		Province province = provinceRepository.findById(1).orElseThrow(RuntimeException::new);
		UUID venetoUuid = findVenetoUuid();
		assertThat(province.region().uuid()).isNotEqualByComparingTo(venetoUuid);

		Province updateProvince = new Province(null, "Update Province",
				new ProvinceLocalData("11", "22"),
				new Province.Region(venetoUuid, null));

		Province result = provinceRepository.update(province.uuid(), updateProvince);

		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("Update Province");
		assertThat(result.region().name()).isEqualTo("Veneto");
		assertThat(result.region().uuid()).isEqualTo(venetoUuid);
		ProvinceLocalData provinceLocalData = result.localData();
		assertThat(provinceLocalData).isNotNull();
		assertThat(provinceLocalData.itCodiceIstat()).isEqualTo("11");
		assertThat(provinceLocalData.itSigla()).isEqualTo("22");
	}

	@Test
	void updateANotExistingProvince() {
		UUID randomUUID = UUID.randomUUID();
		Province updateProvince = new Province(null, "Update Province", null, null);
		assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
						provinceRepository.update(randomUUID, updateProvince))
				.withMessageContaining(randomUUID.toString());
	}

	private UUID findVenetoUuid() {
		Searchable filterForVeneto = FilterCriteria.of(
				FilterCondition.of("region.name", "eq", "Veneto"));
		Page<Province> provincePage = provinceRepository.find(filterForVeneto,
				PageRequest.ofSize(1));
		return provincePage.getContent().get(0).region().uuid();
	}
}