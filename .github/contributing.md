Contributing to SkinChanger
===========================
An outline of what we require from all incoming PRs

Coding Style
------------
We use [Google's Java StyleGuide](https://google.github.io/styleguide/javaguide.html) for this repository. All code follows that structure. When contributing, ensure your code follows the Google Code StyleGuide whilst contributing to this project. 

This is a requirement for all PRs, not a request.

Pull Request Guidelines
-----------------------

What to do:
* Ensure your code follows the Google Java CodeStyle prior to submission.
* Only submit a PR once ALL changes to your modification are complete
* PR titles should be short and should give an outline of what they are for  (e.g. "Add an option for a new skin library #4")
* Refer/mention appropriate issues you are working on. This can either be in the title or in the description of the inital pull request.
* Ensure your code with your changes successfully builds. `gradlew build`

What not to do:
* Use a different code style in your pull request.
* Submit an incomplete or "in progress" pull request.
* Submit a change in a pull request which is not relevant to the pull request.
* Assign users to your pull request - We will do this automatically
* Add your own tags to pull requests - We will do this for you

**Note:**
Pull requests which do not follow these guidelines will be rejected/closed

Before Merging Pull Requests
----------------------------

If a pull request did not pass the Forge CI build, then it should be not be merged.

Contributors (write access):
* May assign tags/authors to review a PR if required
* May request modification of a PR based on the guidelines
* Should **not** merge pull requests even if they are passing

In general leave merging to the [CODEOWNERS](https://github.com/boomboompower/SkinChanger/blob/master/.github/CODEOWNERS) of the pull request.

CODEOWNERS:
* **May** merge the pull request if
  * The PR follows these guidelines
  * The CI detects it as passing
  * The code does not have pending requirements
* **May not** merge the pull request if
  * The PR does not meet the above requirements
  * Contributors have requested changes which have not been resolved
  * *Any* artifact in the CI is failing


Merging Guidelines (what to use when merging)
----------------------------------------------

* Pull Requests with 2 or less commits should be rebased into project rather than merged.
* Pull Requests with 3 or more commits should be applied with ["Squash and Merge"](https://github.com/blog/2141-squash-your-commits) to make code changes in the normal branch more readable.
