Dans le contexte de ton application backend Spring Boot et ton frontend Next.js avec Axios pour les requêtes HTTP, je vais t'expliquer les différents types de paramètres que tu peux utiliser dans un contrôleur Spring, leurs cas d'utilisation, et comment les gérer avec des requêtes Axios depuis le frontend. Je vais couvrir les annotations comme `@RequestBody`, `@RequestParam`, `@PathVariable`, et d'autres, avec des exemples concrets pour chaque cas, ainsi que les types de données à utiliser et quand les appliquer.

---

### 1. Types de paramètres dans un contrôleur Spring

Dans un contrôleur Spring, les paramètres sont utilisés pour récupérer les données envoyées par le frontend dans une requête HTTP. Les annotations Spring définissent comment ces données sont extraites (par exemple, depuis l'URL, le corps de la requête, les en-têtes, etc.). Voici les principaux types d'annotations et leurs cas d'utilisation :

#### a) `@RequestBody`
- **Description** : Utilisé pour récupérer des données envoyées dans le **corps** (body) de la requête HTTP, généralement au format JSON ou XML. C'est typique pour les requêtes POST ou PUT où le frontend envoie des objets complexes.
- **Quand utiliser** :
    - Quand le frontend envoie des données structurées (par exemple, un DTO comme `CommentDTO`) dans le corps de la requête.
    - Pour créer ou mettre à jour des ressources (POST, PUT).
- **Types de données** :
    - Objets Java (par exemple, DTOs comme `CommentDTO`, `UserDTO`).
    - Listes d'objets (par exemple, `List<CommentDTO>`).
- **Cas d'utilisation** :
    - Création d'un commentaire, envoi d'un formulaire complexe, mise à jour d'un profil utilisateur.
- **Exemple** :
  ```java
  @PostMapping("/recipe/{recipeId}/comment")
  public ResponseEntity<CommentDTO> createComment(
          @PathVariable UUID recipeId,
          @RequestBody CommentDTO commentDTO,
          @RequestHeader("X-User-Id") UUID userId) {
      CommentDTO createdComment = commentService.createComment(commentDTO, userId, recipeId);
      return ResponseEntity.ok(createdComment);
  }
  ```
    - **Frontend (Next.js avec Axios)** :
      ```javascript
      import axios from 'axios';
  
      const createComment = async (recipeId, commentData, userId) => {
        try {
          const response = await axios.post(
            `/api/comments/recipe/${recipeId}`,
            commentData, // Corps de la requête (JSON)
            {
              headers: {
                'X-User-Id': userId, // En-tête personnalisé
              },
            }
          );
          return response.data;
        } catch (error) {
          console.error('Erreur lors de la création du commentaire:', error);
        }
      };
  
      // Utilisation
      const commentData = {
        content: 'Super recette !',
        parentId: null,
      };
      createComment('123e4567-e89b-12d3-a456-426614174000', commentData, 'user-id-uuid');
      ```
    - **Explication** : Le frontend envoie un objet JSON (`commentData`) dans le corps de la requête POST. Spring mappe automatiquement ce JSON à l'objet `CommentDTO` grâce à `@RequestBody`.

#### b) `@PathVariable`
- **Description** : Utilisé pour extraire des valeurs de l'**URL** (partie dynamique de l'URI). Par exemple, `/api/comments/{commentId}` extrait `commentId`.
- **Quand utiliser** :
    - Quand une partie de l'URL représente un identifiant ou une ressource spécifique (par exemple, ID d'un commentaire, d'une recette).
    - Pour les requêtes GET, DELETE, ou PUT où l'identifiant est dans l'URL.
- **Types de données** :
    - Types primitifs : `int`, `long`.
    - Types complexes : `UUID`, `String`.
- **Cas d'utilisation** :
    - Récupérer un commentaire spécifique, supprimer une ressource, mettre à jour une entité.
- **Exemple** :
  ```java
  @GetMapping("/{commentId}")
  public ResponseEntity<CommentDTO> getCommentById(@PathVariable Integer commentId) {
      CommentDTO comment = commentService.getCommentById(commentId);
      return ResponseEntity.ok(comment);
  }
  ```
    - **Frontend (Next.js avec Axios)** :
      ```javascript
      import axios from 'axios';
  
      const getComment = async (commentId) => {
        try {
          const response = await axios.get(`/api/comments/${commentId}`);
          return response.data;
        } catch (error) {
          console.error('Erreur lors de la récupération du commentaire:', error);
        }
      };
  
      // Utilisation
      getComment(123);
      ```
    - **Explication** : Le `commentId` est inclus dans l'URL (`/api/comments/123`). Spring extrait cette valeur et la mappe à la variable `commentId`.

#### c) `@RequestParam`
- **Description** : Utilisé pour extraire des paramètres de la **query string** (par exemple, `?key=value`) dans l'URL.
- **Quand utiliser** :
    - Quand le frontend envoie des données simples ou des filtres via la query string.
    - Pour des requêtes GET où les paramètres sont facultatifs ou multiples.
- **Types de données** :
    - Types primitifs : `int`, `long`, `boolean`.
    - Types complexes : `String`, `UUID`.
    - Listes : `List<String>`, `List<Integer>`.
- **Cas d'utilisation** :
    - Filtres de recherche, pagination, tri (par exemple, `?page=1&size=10`).
- **Exemple** :
  ```java
  @GetMapping("/recipe/{recipeId}/comments")
  public ResponseEntity<List<CommentDTO>> getCommentsByRecipeId(
          @PathVariable UUID recipeId,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {
      // Supposons une méthode paginée dans le service
      List<CommentDTO> comments = commentService.getCommentsByRecipeId(recipeId, page, size);
      return ResponseEntity.ok(comments);
  }
  ```
    - **Frontend (Next.js avec Axios)** :
      ```javascript
      import axios from 'axios';
  
      const getComments = async (recipeId, page = 0, size = 10) => {
        try {
          const response = await axios.get(`/api/comments/recipe/${recipeId}`, {
            params: { page, size }, // Paramètres de la query string
          });
          return response.data;
        } catch (error) {
          console.error('Erreur lors de la récupération des commentaires:', error);
        }
      };
  
      // Utilisation
      getComments('123e4567-e89b-12d3-a456-426614174000', 1, 20);
      ```
    - **Explication** : Les paramètres `page` et `size` sont envoyés dans la query string (par exemple, `/api/comments/recipe/123?page=1&size=20`). Spring les mappe aux variables `page` et `size`.

#### d) `@RequestHeader`
- **Description** : Utilisé pour extraire des valeurs des **en-têtes** HTTP de la requête.
- **Quand utiliser** :
    - Quand des métadonnées ou des informations d'authentification (par exemple, token, ID utilisateur) sont envoyées dans les en-têtes.
- **Types de données** :
    - `String`, `UUID`, ou autres types convertibles depuis une chaîne.
- **Cas d'utilisation** :
    - Authentification (par exemple, récupérer un token JWT ou un ID utilisateur).
- **Exemple** :
  ```java
  @PostMapping("/recipe/{recipeId}/comment")
  public ResponseEntity<CommentDTO> createComment(
          @PathVariable UUID recipeId,
          @RequestBody CommentDTO commentDTO,
          @RequestHeader("X-User-Id") UUID userId) {
      CommentDTO createdComment = commentService.createComment(commentDTO, userId, recipeId);
      return ResponseEntity.ok(createdComment);
  }
  ```
    - **Frontend (Next.js avec Axios)** :
      ```javascript
      import axios from 'axios';
  
      const createComment = async (recipeId, commentData, userId) => {
        try {
          const response = await axios.post(
            `/api/comments/recipe/${recipeId}`,
            commentData,
            {
              headers: {
                'X-User-Id': userId, // En-tête personnalisé
              },
            }
          );
          return response.data;
        } catch (error) {
          console.error('Erreur lors de la création du commentaire:', error);
        }
      };
      ```
    - **Explication** : L'en-tête `X-User-Id` contient l'identifiant de l'utilisateur connecté, qui est extrait par Spring avec `@RequestHeader`.

#### e) `@RequestPart` (pour les formulaires multipart)
- **Description** : Utilisé pour gérer les requêtes **multipart/form-data**, souvent pour les fichiers (images, PDF) ou des données combinées avec des fichiers.
- **Quand utiliser** :
    - Quand le frontend envoie des fichiers (par exemple, une image de profil) avec d'autres données.
- **Types de données** :
    - `MultipartFile` pour les fichiers.
    - Objets Java pour les parties JSON ou texte.
- **Cas d'utilisation** :
    - Téléversement d'une image pour une recette ou un profil utilisateur.
- **Exemple** :
  ```java
  @PostMapping(value = "/recipe", consumes = "multipart/form-data")
  public ResponseEntity<RecipeDTO> createRecipe(
          @RequestPart("recipe") RecipeDTO recipeDTO,
          @RequestPart("image") MultipartFile image,
          @RequestHeader("X-User-Id") UUID userId) {
      RecipeDTO createdRecipe = recipeService.createRecipe(recipeDTO, image, userId);
      return ResponseEntity.ok(createdRecipe);
  }
  ```
    - **Frontend (Next.js avec Axios)** :
      ```javascript
      import axios from 'axios';
  
      const createRecipe = async (recipeData, imageFile, userId) => {
        const formData = new FormData();
        formData.append('recipe', JSON.stringify(recipeData)); // Données JSON
        formData.append('image', imageFile); // Fichier image
  
        try {
          const response = await axios.post('/api/recipes', formData, {
            headers: {
              'X-User-Id': userId,
              'Content-Type': 'multipart/form-data',
            },
          });
          return response.data;
        } catch (error) {
          console.error('Erreur lors de la création de la recette:', error);
        }
      };
  
      // Utilisation
      const recipeData = { title: 'Gâteau au chocolat', description: 'Délicieux' };
      const imageFile = document.querySelector('input[type="file"]').files[0];
      createRecipe(recipeData, imageFile, 'user-id-uuid');
      ```
    - **Explication** : Le frontend envoie un `FormData` contenant un objet JSON (`recipe`) et un fichier (`image`). Spring extrait chaque partie avec `@RequestPart`.

---

### 2. Quand utiliser `@RequestBody` vs `@RequestParam`

#### a) Utiliser `@RequestBody`
- **Quand** : Les données sont complexes (objets JSON, tableaux d'objets) et envoyées dans le corps de la requête.
- **Méthodes HTTP** : POST, PUT, PATCH.
- **Exemple de cas** :
    - Création d'un commentaire (`CommentDTO` avec contenu, parentId, etc.).
    - Mise à jour d'un profil utilisateur avec plusieurs champs.
- **Avantages** :
    - Peut gérer des structures complexes.
    - Convient pour des données volumineuses.
- **Inconvénients** :
    - Nécessite un corps de requête, non adapté pour les GET.
    - Nécessite une sérialisation/désérialisation JSON.

#### b) Utiliser `@RequestParam`
- **Quand** : Les données sont simples (valeurs scalaires comme des nombres, chaînes, booléens) et envoyées via la query string.
- **Méthodes HTTP** : Principalement GET, parfois DELETE.
- **Exemple de cas** :
    - Filtres de recherche (par exemple, `?category=vegan&sort=desc`).
    - Pagination (par exemple, `?page=1&size=10`).
- **Avantages** :
    - Simple pour les paramètres facultatifs ou multiples.
    - Pas besoin de corps de requête, idéal pour GET.
- **Inconvénients** :
    - Limité aux données simples (pas d'objets complexes).
    - Longueur de l'URL limitée par les navigateurs.

#### Exemple comparatif
- **Avec `@RequestBody`** (POST pour créer un commentaire) :
  ```java
  @PostMapping("/comment")
  public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
      return ResponseEntity.ok(commentService.createComment(commentDTO));
  }
  ```
    - **Axios** :
      ```javascript
      axios.post('/api/comment', { content: 'Super !', parentId: null });
      ```

- **Avec `@RequestParam`** (GET pour filtrer des commentaires) :
  ```java
  @GetMapping("/comments")
  public ResponseEntity<List<CommentDTO>> getComments(
          @RequestParam UUID recipeId,
          @RequestParam(defaultValue = "0") int page) {
      return ResponseEntity.ok(commentService.getCommentsByRecipeId(recipeId, page));
  }
  ```
    - **Axios** :
      ```javascript
      axios.get('/api/comments', { params: { recipeId: '123e4567-e89b-12d3-a456-426614174000', page: 1 } });
      ```

---

### 3. Tous les types de paramètres et cas d'utilisation

Voici un récapitulatif complet des annotations de paramètres dans Spring, leurs types de données, et leurs cas d'utilisation :

| **Annotation**      | **Type de données**                     | **Cas d'utilisation**                                                                 | **Exemple (Backend)**                                                                 | **Exemple (Axios)**                                                                 |
|---------------------|-----------------------------------------|-------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| `@RequestBody`      | Objets (DTO), Listes                    | Création/mise à jour de ressources complexes (JSON) via POST/PUT                     | `@RequestBody CommentDTO commentDTO`                                                  | `axios.post('/api/comments', commentData)`                                           |
| `@PathVariable`     | `int`, `long`, `String`, `UUID`         | Récupérer des identifiants ou parties dynamiques de l'URL (GET, DELETE, PUT)         | `@PathVariable UUID recipeId`                                                         | `axios.get('/api/comments/${recipeId}')`                                            |
| `@RequestParam`     | `int`, `long`, `String`, `UUID`, Listes | Filtres, pagination, paramètres simples dans la query string (GET)                   | `@RequestParam(defaultValue = "0") int page`                                          | `axios.get('/api/comments', { params: { page: 1 } })`                               |
| `@RequestHeader`    | `String`, `UUID`, autres convertibles   | Récupérer des en-têtes (authentification, métadonnées)                               | `@RequestHeader("X-User-Id") UUID userId`                                             | `axios.post('/api/comments', data, { headers: { 'X-User-Id': userId } })`           |
| `@RequestPart`      | `MultipartFile`, Objets (DTO)           | Téléversement de fichiers ou données multipart (POST)                                | `@RequestPart("image") MultipartFile image`                                           | `formData.append('image', file); axios.post('/api/recipes', formData)`              |
| `@ModelAttribute`   | Objets (formulaires non-JSON)           | Formulaires HTML traditionnels ou données non-JSON (rare avec Next.js/Axios)         | `@ModelAttribute UserDTO userDTO`                                                     | Rare avec Axios, utilisé avec `application/x-www-form-urlencoded`                   |
| `@MatrixVariable`   | `String`, Listes                        | Paramètres dans des segments d'URL (par exemple, `/users;color=red`)                | `@MatrixVariable("color") String color`                                               | Rare, non recommandé pour Next.js                                                   |
| `@CookieValue`      | `String`, autres convertibles           | Récupérer des valeurs de cookies                                                     | `@CookieValue("sessionId") String sessionId`                                          | `axios.get('/api/data', { withCredentials: true })`                                  |

---

### 4. Bonnes pratiques pour Next.js avec Axios
- **Cohérence des types** :
    - Utilise des `UUID` pour les identifiants (comme `recipeId`, `userId`) pour éviter les collisions et assurer la compatibilité avec ta base de données.
    - Pour les paramètres simples (pagination, filtres), utilise `int` ou `String` dans `@RequestParam`.
- **Headers pour authentification** :
    - Envoie toujours un token ou un identifiant utilisateur dans les en-têtes (`X-User-Id` ou `Authorization`) pour sécuriser les requêtes.
    - Exemple : `axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;`
- **Gestion des erreurs** :
    - Utilise la `GlobalExceptionHandler` (comme dans ton code) pour renvoyer des réponses JSON cohérentes (par exemple, `{ status: 400, error: "Bad Request", message: "Comment not found" }`).
    - Côté frontend, capture les erreurs avec `try/catch` :
      ```javascript
      try {
        const response = await axios.get('/api/comments/123');
        return response.data;
      } catch (error) {
        console.error(error.response.data.message); // Affiche le message d'erreur du backend
      }
      ```
- **WebSocket pour temps réel** :
    - Pour les notifications et commentaires en temps réel, utilise `stomp.js` avec `SockJS` (comme montré dans la réponse précédente) pour gérer les abonnements WebSocket.
    - Exemple : Souscrire à `/topic/comments/{recipeId}` pour les commentaires en temps réel.

---

### 5. Résumé
- **Utilise `@RequestBody`** pour les données complexes (JSON) dans les requêtes POST/PUT.
- **Utilise `@RequestParam`** pour les paramètres simples dans la query string (GET).
- **Utilise `@PathVariable`** pour les identifiants dans l'URL.
- **Utilise `@RequestHeader`** pour les métadonnées ou l'authentification.
- **Utilise `@RequestPart`** pour les fichiers ou données multipart.
- **Frontend (Axios)** : Adapte la structure des requêtes (corps, paramètres, en-têtes) au type d'annotation utilisé dans le contrôleur.
- **Types de données** : Aligne les types Java (`UUID`, `int`, `String`) avec les données envoyées par le frontend pour éviter les erreurs de conversion.

Si tu as besoin d'autres exemples spécifiques ou d'une clarification sur un cas particulier (par exemple, un endpoint complexe), fais-le-moi savoir !