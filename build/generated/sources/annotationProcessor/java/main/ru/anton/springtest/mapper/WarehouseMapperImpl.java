package ru.anton.springtest.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.anton.springtest.dto.ProductCreateDto;
import ru.anton.springtest.dto.ProductResponseDto;
import ru.anton.springtest.dto.ProductUpdateDto;
import ru.anton.springtest.dto.WarehouseCreateDto;
import ru.anton.springtest.dto.WarehouseResponseDto;
import ru.anton.springtest.dto.WarehouseUpdateDto;
import ru.anton.springtest.model.Product;
import ru.anton.springtest.model.Warehouse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-08T14:29:09+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.4.1.jar, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class WarehouseMapperImpl implements WarehouseMapper {

    @Override
    public WarehouseResponseDto toResponseDto(Warehouse warehouse) {
        if ( warehouse == null ) {
            return null;
        }

        WarehouseResponseDto warehouseResponseDto = new WarehouseResponseDto();

        warehouseResponseDto.setId( warehouse.getId() );
        warehouseResponseDto.setName( warehouse.getName() );
        warehouseResponseDto.setProducts( productListToProductResponseDtoList( warehouse.getProducts() ) );

        return warehouseResponseDto;
    }

    @Override
    public List<WarehouseResponseDto> toResponseDtoList(List<Warehouse> warehouses) {
        if ( warehouses == null ) {
            return null;
        }

        List<WarehouseResponseDto> list = new ArrayList<WarehouseResponseDto>( warehouses.size() );
        for ( Warehouse warehouse : warehouses ) {
            list.add( toResponseDto( warehouse ) );
        }

        return list;
    }

    @Override
    public Warehouse toEntity(WarehouseCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Warehouse warehouse = new Warehouse();

        warehouse.setName( dto.getName() );
        warehouse.setProducts( productCreateDtoListToProductList( dto.getProducts() ) );

        return warehouse;
    }

    @Override
    public void updateEntity(WarehouseUpdateDto dto, Warehouse warehouse) {
        if ( dto == null ) {
            return;
        }

        warehouse.setName( dto.getName() );
        if ( warehouse.getProducts() != null ) {
            List<Product> list = productUpdateDtoListToProductList( dto.getProducts() );
            if ( list != null ) {
                warehouse.getProducts().clear();
                warehouse.getProducts().addAll( list );
            }
            else {
                warehouse.setProducts( null );
            }
        }
        else {
            List<Product> list = productUpdateDtoListToProductList( dto.getProducts() );
            if ( list != null ) {
                warehouse.setProducts( list );
            }
        }
    }

    @Override
    public void updateProductEntity(ProductUpdateDto dto, Product product) {
        if ( dto == null ) {
            return;
        }

        product.setTitle( dto.getTitle() );
        product.setPrice( dto.getPrice() );
    }

    protected ProductResponseDto productToProductResponseDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponseDto productResponseDto = new ProductResponseDto();

        productResponseDto.setId( product.getId() );
        productResponseDto.setTitle( product.getTitle() );
        productResponseDto.setPrice( product.getPrice() );

        return productResponseDto;
    }

    protected List<ProductResponseDto> productListToProductResponseDtoList(List<Product> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductResponseDto> list1 = new ArrayList<ProductResponseDto>( list.size() );
        for ( Product product : list ) {
            list1.add( productToProductResponseDto( product ) );
        }

        return list1;
    }

    protected Product productCreateDtoToProduct(ProductCreateDto productCreateDto) {
        if ( productCreateDto == null ) {
            return null;
        }

        Product product = new Product();

        product.setTitle( productCreateDto.getTitle() );
        product.setPrice( productCreateDto.getPrice() );

        return product;
    }

    protected List<Product> productCreateDtoListToProductList(List<ProductCreateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Product> list1 = new ArrayList<Product>( list.size() );
        for ( ProductCreateDto productCreateDto : list ) {
            list1.add( productCreateDtoToProduct( productCreateDto ) );
        }

        return list1;
    }

    protected Product productUpdateDtoToProduct(ProductUpdateDto productUpdateDto) {
        if ( productUpdateDto == null ) {
            return null;
        }

        Product product = new Product();

        product.setId( productUpdateDto.getId() );
        product.setTitle( productUpdateDto.getTitle() );
        product.setPrice( productUpdateDto.getPrice() );

        return product;
    }

    protected List<Product> productUpdateDtoListToProductList(List<ProductUpdateDto> list) {
        if ( list == null ) {
            return null;
        }

        List<Product> list1 = new ArrayList<Product>( list.size() );
        for ( ProductUpdateDto productUpdateDto : list ) {
            list1.add( productUpdateDtoToProduct( productUpdateDto ) );
        }

        return list1;
    }
}
