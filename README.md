# SearchButton
## SearchButton 带动画的搜索按钮

### 使用

### gradle

```xml
implementation 'top.golabe.SearchButton:library:1.0.0'
```

### xml

```xml
  <com.github.golabe.searchbutton.library.SearchButton
        android:id="@+id/search_button"
        app:search_border="3dp"
        app:search_color="@android:color/white"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_centerInParent="true"
        app:search_duration="500" />
```
### attrs

### java
```java
  startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchButton.isSearching()){
                    searchButton.start();
                }

            }
        });
        endSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchButton.isSearching()){
                    searchButton.searchOver();
                }

            }
        });
```

```xml
  <declare-styleable name="SearchButton">
        <attr name="search_color" format="color" />
        <attr name="search_border" format="dimension" />
        <attr name="search_duration" format="integer" />
    </declare-styleable>
```
